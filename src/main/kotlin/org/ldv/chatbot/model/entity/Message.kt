package org.ldv.chatbot.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity
class Message(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var id: Long? = null,

    @Column(columnDefinition = "TEXT", nullable = false)
    var contenu: String,

    @Column(nullable = false)
    var dateCreation: LocalDateTime,

    // STRING : on enregistre le nom (« UTILISATEUR ») et non un numéro (1)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var typeMessage: TypeMessage,

    // Plusieurs messages appartiennent à une conversation
    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    var conversation: Conversation
)