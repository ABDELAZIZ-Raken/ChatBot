package org.ldv.chatbot.service

import org.ldv.chatbot.model.Conversation
import org.ldv.chatbot.model.Message
import org.springframework.stereotype.Service

@Service
class ConversationService {

    // Conversations de démonstration de la maquette (compte mail@gmail.com)
    private val conversations = mutableListOf(
        Conversation(
            1, "Révision SQL – jointures", "Mistral Small 4", "mail@gmail.com",
            mutableListOf(
                Message(true, "Peux-tu m’expliquer la différence entre INNER JOIN et LEFT JOIN ?"),
                Message(false, """<h4>INNER JOIN et LEFT JOIN</h4><b>INNER JOIN</b> ne garde que les lignes ayant une correspondance dans les deux tables. <b>LEFT JOIN</b> garde toutes les lignes de la table de gauche, avec <code>NULL</code> s’il n’y a pas de correspondance.<pre>SELECT c.titre, u.email
FROM conversation c
LEFT JOIN utilisateur u ON u.id = c.utilisateur_id;</pre>""")
            )
        ),
        Conversation(
            2, "Plan de dissertation", "Claude Haiku 4.5", "mail@gmail.com",
            mutableListOf(
                Message(true, "Aide-moi à faire un plan en trois parties."),
                Message(false, "Bien sûr : donne-moi le sujet et la problématique.")
            )
        ),
        Conversation(
            3, "Explication de Spring MVC", "Gemini 2.5 Flash-Lite", "mail@gmail.com",
            mutableListOf(
                Message(true, "C’est quoi un contrôleur ?"),
                Message(false, "Une classe qui reçoit les requêtes HTTP et choisit la vue à afficher.")
            )
        )
    )

    private var prochainId = 4

    // Les conversations d'un compte, la plus récente en premier
    fun deUtilisateur(email: String): List<Conversation> {
        return conversations.filter { it.proprietaire.equals(email, ignoreCase = true) }
    }

    // Une conversation, uniquement si elle appartient bien au compte
    fun trouver(email: String, id: Int): Conversation? {
        return deUtilisateur(email).firstOrNull { it.id == id }
    }

    // Nouvelle conversation vide (le modèle est fixé pour toute la conversation)
    fun creer(email: String, modele: String, titre: String): Conversation {
        val titreSaisi = titre.trim()
        val conversation = Conversation(
            prochainId++,
            titreSaisi.ifEmpty { "Nouvelle conversation" },
            modele,
            email,
            mutableListOf(),
            titreSaisi.isEmpty()
        )
        conversations.add(0, conversation)
        return conversation
    }

    // Ajoute le message de l'élève puis la réponse simulée (comme la maquette)
    fun envoyer(email: String, id: Int, texte: String) {
        val conversation = trouver(email, id) ?: return
        val message = texte.trim()
        if (message.isEmpty()) return

        // Sans titre saisi, le titre vient du premier message (40 caractères)
        if (conversation.titreAuto && conversation.messages.isEmpty()) {
            conversation.titre = message.take(40)
            conversation.titreAuto = false
        }

        conversation.messages.add(Message(true, message))
        conversation.messages.add(
            Message(false, "Réponse simulée de ${conversation.modele} : c’est une maquette, la vraie réponse viendra de l’API.")
        )
    }

    // Nombre de conversations qui utilisent un modèle (page Détails)
    fun compterParModele(nomModele: String): Int {
        return conversations.count { it.modele == nomModele }
    }

    fun supprimer(email: String, id: Int) {
        conversations.removeIf { it.id == id && it.proprietaire.equals(email, ignoreCase = true) }
    }
}