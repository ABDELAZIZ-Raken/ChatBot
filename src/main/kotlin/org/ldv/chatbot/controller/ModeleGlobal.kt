package org.ldv.chatbot.controller

import jakarta.servlet.http.HttpSession
import org.ldv.chatbot.model.Compte
import org.ldv.chatbot.service.ModeleService
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute

@ControllerAdvice
class ModeleGlobal(private val modeleService: ModeleService) {

    // Le compte connecté (null pour un visiteur), disponible dans toutes les pages
    @ModelAttribute("compte")
    fun compte(session: HttpSession): Compte? {
        return session.getAttribute("compte") as? Compte
    }

    // Premier modèle, pour le lien « Détails » du header
    @ModelAttribute("detailId")
    fun detailId(): Int? {
        return modeleService.tous().firstOrNull()?.id
    }
}