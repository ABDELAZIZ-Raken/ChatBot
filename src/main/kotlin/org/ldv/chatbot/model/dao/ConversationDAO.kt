package org.ldv.chatbot.model.dao

import org.ldv.chatbot.model.entity.Conversation
import org.ldv.chatbot.model.entity.ModeleIA
import org.springframework.data.jpa.repository.JpaRepository

interface ConversationDAO : JpaRepository<Conversation, Long> {

    // Les conversations, la plus récemment modifiée en premier
    fun findAllByOrderByDateDerniereModificationDesc(): List<Conversation>

    // Nombre de conversations qui utilisent un modèle
    fun countByModele(modele: ModeleIA): Long
}