package org.ldv.chatbot.model.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    // Nom affiché dans les templates (ex. « Administrateur »)
    var nom: String,

    // Nom utilisé par Spring Security (ex. « ROLE_ADMIN »)
    var nomLogique: String
)