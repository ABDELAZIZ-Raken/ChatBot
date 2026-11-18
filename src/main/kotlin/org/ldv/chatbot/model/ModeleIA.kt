package org.ldv.chatbot.model

// Provisoire : sera remplacé par l'entité JPA Modele
class ModeleIA(
    val id: Int,
    var nom: String,
    var fournisseur: String,
    var identifiant: String,    // identifiant technique de l'API
    var urlApi: String,
    var description: String,
    var actif: Boolean
) {
    // Initiales affichées dans le rond (ex. « Mistral Small 4 » → « MS »)
    val initiales: String
        get() = nom.trim().split(Regex("\\s+"))
            .mapNotNull { it.firstOrNull() }
            .joinToString("")
            .take(2)
            .uppercase()
}