package org.ldv.chatbot.controller

import org.ldv.chatbot.model.dao.ModeleIADAO
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ChatBotController(
    private val modeleIADAO: ModeleIADAO
) {

    // ----- Accueil (visible aussi par les visiteurs) -----

    @GetMapping("/", "/accueil")
    fun accueil(model: Model): String {
        model.addAttribute("page", "accueil")
        // Les modèles actifs, lus dans la base de données
        model.addAttribute("modeles", modeleIADAO.findByActifTrue())
        return "utilisateur/accueil"
    }

    // ----- Connexion -----
    // Seul l'affichage est ici : le traitement du formulaire (POST /login)
    // est fait par Spring Security.

    @GetMapping("/login")
    fun login(model: Model): String {
        model.addAttribute("page", "connexion")
        return "auth/login"
    }

    // ----- Plan du site -----
    // Réservé à l'administrateur : la règle est dans SecurityConfig.

    @GetMapping("/plan-du-site")
    fun planDuSite(model: Model): String {
        model.addAttribute("page", "plan")
        return "utilisateur/plan-du-site"
    }
}