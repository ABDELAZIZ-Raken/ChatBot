package org.ldv.chatbot.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ChatBotController {

    @GetMapping("/")
    fun accueil(): String {
        return "utilisateur/accueil"
    }

    @GetMapping("/connexion")
    fun connexion(): String {
        return "auth/connexion"
    }
}