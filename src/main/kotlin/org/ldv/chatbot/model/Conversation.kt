package org.ldv.chatbot.model

// Provisoire : sera remplacé par les entités JPA Conversation et Message
data class Message(
    val utilisateur: Boolean,   // true = message de l'élève, false = réponse de l'IA
    val contenu: String
)

class Conversation(
    val id: Int,
    var titre: String,
    val modele: String,
    val proprietaire: String,            // e-mail du compte
    val messages: MutableList<Message>,
    var titreAuto: Boolean = false       // titre pris sur le premier message
)