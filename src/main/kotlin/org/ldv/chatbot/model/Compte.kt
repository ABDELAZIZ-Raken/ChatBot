package org.ldv.chatbot.model

// Compte provisoire (en mémoire). Sera remplacé par l'entité Utilisateur (JPA).
data class Compte(
    val email: String,
    val motDePasse: String,
    val role: String            // "UTILISATEUR" ou "ADMIN"
) {
    val estAdmin: Boolean
        get() = role == "ADMIN"
}