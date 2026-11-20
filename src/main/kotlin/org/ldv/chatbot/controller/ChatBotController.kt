package org.ldv.chatbot.controller

import jakarta.servlet.http.HttpSession
import org.ldv.chatbot.model.Compte
import org.ldv.chatbot.model.dao.ModeleIADAO
import org.ldv.chatbot.service.CompteService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ChatBotController(
    private val compteService: CompteService,
    private val modeleIADAO: ModeleIADAO
) {

    // ----- Accueil -----

    @GetMapping("/", "/accueil")
    fun accueil(model: Model): String {
        model.addAttribute("page", "accueil")
        // Les modèles actifs, lus dans la base de données
        model.addAttribute("modeles", modeleIADAO.findByActifTrue())
        return "utilisateur/accueil"
    }

    // ----- Connexion -----

    @GetMapping("/connexion")
    fun connexion(model: Model, session: HttpSession): String {
        if (session.getAttribute("compte") != null) return "redirect:/chat"
        model.addAttribute("page", "connexion")
        model.addAttribute("inscription", false)
        return "auth/connexion"
    }

    @PostMapping("/connexion")
    fun connecter(
        @RequestParam(defaultValue = "") email: String,
        @RequestParam(defaultValue = "") password: String,
        session: HttpSession,
        model: Model
    ): String {
        var erreur: String? = null

        if (email.isBlank() || password.isEmpty()) {
            erreur = "Saisissez votre adresse e-mail et votre mot de passe."
        } else {
            val compte = compteService.authentifier(email, password)
            if (compte == null) {
                erreur = "Adresse e-mail ou mot de passe incorrect."
            } else {
                session.setAttribute("compte", compte)
                return "redirect:/chat"
            }
        }

        model.addAttribute("page", "connexion")
        model.addAttribute("inscription", false)
        model.addAttribute("erreur", erreur)
        return "auth/connexion"
    }

    // ----- Inscription -----

    @GetMapping("/inscription")
    fun inscription(model: Model, session: HttpSession): String {
        if (session.getAttribute("compte") != null) return "redirect:/chat"
        model.addAttribute("page", "connexion")
        model.addAttribute("inscription", true)
        return "auth/connexion"
    }

    @PostMapping("/inscription")
    fun inscrire(
        @RequestParam(defaultValue = "") email: String,
        @RequestParam(defaultValue = "") password: String,
        @RequestParam(defaultValue = "") confirmation: String,
        session: HttpSession,
        model: Model
    ): String {
        val erreur: String? = when {
            email.isBlank() || password.isEmpty() ->
                "Saisissez votre adresse e-mail et votre mot de passe."
            compteService.existe(email) ->
                "Un compte existe déjà avec cette adresse e-mail."
            password != confirmation ->
                "Les deux mots de passe sont différents."
            else -> null
        }

        if (erreur == null) {
            session.setAttribute("compte", compteService.inscrire(email, password))
            return "redirect:/chat"
        }

        model.addAttribute("page", "connexion")
        model.addAttribute("inscription", true)
        model.addAttribute("erreur", erreur)
        return "auth/connexion"
    }

    // ----- Déconnexion -----

    @PostMapping("/deconnexion")
    fun deconnecter(session: HttpSession): String {
        session.invalidate()
        return "redirect:/"
    }

    // ----- Plan du site (réservé à l'administrateur) -----

    @GetMapping("/plan-du-site")
    fun planDuSite(model: Model, session: HttpSession): String {
        val compte = session.getAttribute("compte") as? Compte ?: return "redirect:/connexion"
        if (!compte.estAdmin) return "redirect:/chat"

        model.addAttribute("page", "plan")
        return "utilisateur/plan-du-site"
    }
}