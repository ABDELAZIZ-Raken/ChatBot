package org.ldv.chatbot.controller

import org.ldv.chatbot.model.dao.ConversationDAO
import org.ldv.chatbot.model.dao.FournisseurIADAO
import org.ldv.chatbot.model.dao.ModeleIADAO
import org.ldv.chatbot.model.entity.ModeleIA
import org.ldv.chatbot.service.CompteService
import org.ldv.chatbot.service.LogoService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import org.ldv.chatbot.model.dao.UtilisateurDAO
import org.springframework.security.core.Authentication

// L'accès à /administration/** est réservé à ROLE_ADMIN dans SecurityConfig
@Controller
@RequestMapping("/administration")
class AdminController(
    private val modeleIADAO: ModeleIADAO,
    private val fournisseurIADAO: FournisseurIADAO,
    private val conversationDAO: ConversationDAO,
    private val utilisateurDAO: UtilisateurDAO,
    private val logoService: LogoService
) {

    // ----- Onglet Modèles d'IA -----

    @GetMapping
    fun modeles(model: Model): String {
        model.addAttribute("page", "administration")
        model.addAttribute("onglet", "modeles")
        model.addAttribute("modeles", modeleIADAO.findAll())
        model.addAttribute("fournisseurs", fournisseurIADAO.findAll())
        return "admin/administration"
    }

    @PostMapping("/modeles")
    fun creer(
        @RequestParam(defaultValue = "") nom: String,
        @RequestParam fournisseurId: Long,
        @RequestParam(defaultValue = "") identifiant: String,
        @RequestParam(defaultValue = "") urlApi: String,
        @RequestParam(defaultValue = "") description: String,
        @RequestParam(defaultValue = "false") actif: Boolean,
        @RequestParam(required = false) logo: MultipartFile?
    ): String {
        val fournisseur = fournisseurIADAO.findById(fournisseurId).orElse(null)
        if (nom.isNotBlank() && identifiant.isNotBlank() && fournisseur != null) {
            modeleIADAO.save(
                ModeleIA(
                    nom = nom.trim(),
                    identifiantTechnique = identifiant.trim(),
                    urlApi = urlApi.trim(),
                    description = description.trim().ifEmpty { null },
                    cheminLogo = logoService.enregistrer(logo),
                    actif = actif,
                    fournisseur = fournisseur
                )
            )
        }
        return "redirect:/administration"
    }

    @PostMapping("/modeles/{id}")
    fun modifier(
        @PathVariable id: Long,
        @RequestParam(defaultValue = "") nom: String,
        @RequestParam fournisseurId: Long,
        @RequestParam(defaultValue = "") identifiant: String,
        @RequestParam(defaultValue = "") urlApi: String,
        @RequestParam(defaultValue = "") description: String,
        @RequestParam(defaultValue = "false") actif: Boolean,
        @RequestParam(required = false) logo: MultipartFile?
    ): String {
        val modele = modeleIADAO.findById(id).orElse(null) ?: return "redirect:/administration"
        val fournisseur = fournisseurIADAO.findById(fournisseurId).orElse(null)

        if (nom.isNotBlank() && identifiant.isNotBlank() && fournisseur != null) {
            modele.nom = nom.trim()
            modele.identifiantTechnique = identifiant.trim()
            modele.urlApi = urlApi.trim()
            modele.description = description.trim().ifEmpty { null }
            modele.actif = actif
            modele.fournisseur = fournisseur

            // Le nouveau logo remplace l'ancien : on supprime l'ancien fichier
            val nouveauLogo = logoService.enregistrer(logo)
            if (nouveauLogo != null) {
                logoService.supprimer(modele.cheminLogo)
                modele.cheminLogo = nouveauLogo
            }

            modeleIADAO.save(modele)
        }
        return "redirect:/administration"
    }

    // Activer / désactiver (depuis la liste ou depuis la page Détails)
    @PostMapping("/modeles/{id}/statut")
    fun basculer(
        @PathVariable id: Long,
        @RequestParam(defaultValue = "false") detail: Boolean
    ): String {
        val modele = modeleIADAO.findById(id).orElse(null) ?: return "redirect:/administration"
        modele.actif = !modele.actif
        modeleIADAO.save(modele)

        return if (detail) "redirect:/administration/modeles/$id" else "redirect:/administration"
    }

    @PostMapping("/modeles/{id}/supprimer")
    fun supprimer(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        val modele = modeleIADAO.findById(id).orElse(null) ?: return "redirect:/administration"

        // Un modèle utilisé par des conversations ne peut pas être supprimé
        if (conversationDAO.countByModele(modele) > 0) {
            redirectAttributes.addFlashAttribute(
                "erreur",
                "Ce modèle est utilisé par des conversations : désactivez-le plutôt que de le supprimer."
            )
            return "redirect:/administration"
        }

        logoService.supprimer(modele.cheminLogo)
        modeleIADAO.delete(modele)
        return "redirect:/administration"
    }

    // ----- Page Détails d'un modèle -----

    @GetMapping("/modeles/{id}")
    fun detail(@PathVariable id: Long, model: Model): String {
        val modele = modeleIADAO.findById(id).orElse(null) ?: return "redirect:/administration"
        model.addAttribute("page", "detail")
        model.addAttribute("modele", modele)
        model.addAttribute("nbConversations", conversationDAO.countByModele(modele))
        return "admin/detail"
    }

    // ----- Onglet Utilisateurs (provisoire : sera branché sur UtilisateurDAO) -----

    // ----- Onglet Utilisateurs -----

    @GetMapping("/utilisateurs")
    fun utilisateurs(model: Model): String {
        model.addAttribute("page", "administration")
        model.addAttribute("onglet", "utilisateurs")
        // Tous les comptes, lus dans la base de données
        model.addAttribute("utilisateurs", utilisateurDAO.findAll())
        return "admin/administration"
    }

    @PostMapping("/utilisateurs/supprimer")
    fun supprimerUtilisateur(
        @RequestParam id: Long,
        authentication: Authentication
    ): String {
        val cible = utilisateurDAO.findById(id).orElse(null)

        // On ne supprime ni un administrateur, ni son propre compte
        if (cible != null && !cible.estAdmin && cible.email != authentication.name) {
            utilisateurDAO.delete(cible)
        }
        return "redirect:/administration/utilisateurs"
    }
}
