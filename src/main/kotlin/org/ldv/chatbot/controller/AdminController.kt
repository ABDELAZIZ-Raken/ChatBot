package org.ldv.chatbot.controller

import jakarta.servlet.http.HttpSession
import org.ldv.chatbot.model.Compte
import org.ldv.chatbot.service.CompteService
import org.ldv.chatbot.service.ConversationService
import org.ldv.chatbot.service.ModeleService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/administration")
class AdminController(
    private val modeleService: ModeleService,
    private val compteService: CompteService,
    private val conversationService: ConversationService
) {

    private val fournisseurs = listOf("Mistral", "Anthropic", "OpenAI", "Google")

    // Retourne null si le compte est administrateur, sinon la redirection à faire
    // (comme la maquette : visiteur → connexion, utilisateur → chat)
    private fun refuser(session: HttpSession): String? {
        val compte = session.getAttribute("compte") as? Compte ?: return "redirect:/connexion"
        return if (compte.estAdmin) null else "redirect:/chat"
    }

    // ----- Onglet Modèles d'IA -----

    @GetMapping
    fun modeles(model: Model, session: HttpSession): String {
        refuser(session)?.let { return it }
        model.addAttribute("page", "administration")
        model.addAttribute("onglet", "modeles")
        model.addAttribute("modeles", modeleService.tous())
        model.addAttribute("fournisseurs", fournisseurs)
        return "admin/administration"
    }

    @PostMapping("/modeles")
    fun creer(
        @RequestParam(defaultValue = "") nom: String,
        @RequestParam(defaultValue = "Mistral") fournisseur: String,
        @RequestParam(defaultValue = "") identifiant: String,
        @RequestParam(defaultValue = "") urlApi: String,
        @RequestParam(defaultValue = "") description: String,
        @RequestParam(defaultValue = "false") actif: Boolean,
        session: HttpSession
    ): String {
        refuser(session)?.let { return it }
        if (nom.isNotBlank() && identifiant.isNotBlank()) {
            modeleService.creer(nom, fournisseur, identifiant, urlApi, description, actif)
        }
        return "redirect:/administration"
    }

    @PostMapping("/modeles/{id}")
    fun modifier(
        @PathVariable id: Int,
        @RequestParam(defaultValue = "") nom: String,
        @RequestParam(defaultValue = "Mistral") fournisseur: String,
        @RequestParam(defaultValue = "") identifiant: String,
        @RequestParam(defaultValue = "") urlApi: String,
        @RequestParam(defaultValue = "") description: String,
        @RequestParam(defaultValue = "false") actif: Boolean,
        session: HttpSession
    ): String {
        refuser(session)?.let { return it }
        if (nom.isNotBlank() && identifiant.isNotBlank()) {
            modeleService.modifier(id, nom, fournisseur, identifiant, urlApi, description, actif)
        }
        return "redirect:/administration"
    }

    // Activer / désactiver (depuis la liste ou depuis la page Détails)
    @PostMapping("/modeles/{id}/statut")
    fun basculer(
        @PathVariable id: Int,
        @RequestParam(defaultValue = "false") detail: Boolean,
        session: HttpSession
    ): String {
        refuser(session)?.let { return it }
        modeleService.basculer(id)
        return if (detail) "redirect:/administration/modeles/$id" else "redirect:/administration"
    }

    @PostMapping("/modeles/{id}/supprimer")
    fun supprimer(@PathVariable id: Int, session: HttpSession): String {
        refuser(session)?.let { return it }
        modeleService.supprimer(id)
        return "redirect:/administration"
    }

    // ----- Page Détails d'un modèle -----

    @GetMapping("/modeles/{id}")
    fun detail(@PathVariable id: Int, model: Model, session: HttpSession): String {
        refuser(session)?.let { return it }
        val modele = modeleService.trouver(id) ?: return "redirect:/administration"
        model.addAttribute("page", "detail")
        model.addAttribute("modele", modele)
        model.addAttribute("nbConversations", conversationService.compterParModele(modele.nom))
        return "admin/detail"
    }

    // ----- Onglet Utilisateurs -----

    @GetMapping("/utilisateurs")
    fun utilisateurs(model: Model, session: HttpSession): String {
        refuser(session)?.let { return it }
        model.addAttribute("page", "administration")
        model.addAttribute("onglet", "utilisateurs")
        model.addAttribute("utilisateurs", compteService.tous())
        return "admin/administration"
    }

    @PostMapping("/utilisateurs/supprimer")
    fun supprimerUtilisateur(@RequestParam email: String, session: HttpSession): String {
        refuser(session)?.let { return it }
        compteService.supprimer(email)
        return "redirect:/administration/utilisateurs"
    }
}