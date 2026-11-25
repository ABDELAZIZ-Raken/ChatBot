package org.ldv.chatbot.controller

import org.ldv.chatbot.model.dao.ModeleIADAO
import org.ldv.chatbot.model.dao.UtilisateurDAO
import org.ldv.chatbot.model.entity.Utilisateur
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute

@ControllerAdvice
class ModeleGlobal(
    private val modeleIADAO: ModeleIADAO,
    private val utilisateurDAO: UtilisateurDAO
) {

    // L'utilisateur connecté (null pour un visiteur), disponible dans toutes les pages
    @ModelAttribute("utilisateur")
    fun utilisateur(): Utilisateur? {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication == null
            || !authentication.isAuthenticated
            || authentication is AnonymousAuthenticationToken
        ) {
            return null
        }
        return utilisateurDAO.findByEmailIgnoreCase(authentication.name)
    }

    // Premier modèle, pour le lien « Détails » du header
    @ModelAttribute("detailId")
    fun detailId(): Long? {
        return modeleIADAO.findAll().firstOrNull()?.id
    }
}