package org.ldv.chatbot.controller

import jakarta.servlet.http.HttpSession
import org.ldv.chatbot.model.dao.ConversationDAO
import org.ldv.chatbot.model.dao.MessageDAO
import org.ldv.chatbot.model.dao.ModeleIADAO
import org.ldv.chatbot.model.entity.Conversation
import org.ldv.chatbot.model.entity.Message
import org.ldv.chatbot.model.entity.TypeMessage
import org.ldv.chatbot.service.IAServiceFactory
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDateTime

@Controller
class ChatController(
    private val conversationDAO: ConversationDAO,
    private val messageDAO: MessageDAO,
    private val modeleIADAO: ModeleIADAO,
    private val iaServiceFactory: IAServiceFactory
) {

    private val logger = LoggerFactory.getLogger(ChatController::class.java)

    // Titre donné quand l'élève n'en saisit pas : il est remplacé par son 1er message
    private val titreParDefaut = "Nouvelle conversation"

    private fun estConnecte(session: HttpSession): Boolean {
        return session.getAttribute("compte") != null
    }

    // ----- Afficher le chat -----

    @GetMapping("/chat")
    fun chat(
        @RequestParam(required = false) id: Long?,
        model: Model,
        session: HttpSession
    ): String {
        if (!estConnecte(session)) return "redirect:/connexion"

        val conversations = conversationDAO.findAllByOrderByDateDerniereModificationDesc()
        // Conversation choisie dans l'URL (/chat?id=2), sinon la plus récente
        val courante = conversations.firstOrNull { it.id == id } ?: conversations.firstOrNull()

        model.addAttribute("page", "chat")
        model.addAttribute("conversations", conversations)
        model.addAttribute("courante", courante)
        model.addAttribute("modeles", modeleIADAO.findByActifTrue())
        return "utilisateur/chat"
    }

    // ----- Créer une conversation -----

    @PostMapping("/chat/nouvelle")
    fun nouvelleConversation(
        @RequestParam modeleId: Long,
        @RequestParam(defaultValue = "") titre: String,
        session: HttpSession
    ): String {
        if (!estConnecte(session)) return "redirect:/connexion"

        // Le modèle doit exister et être actif
        val modele = modeleIADAO.findById(modeleId).orElse(null)
        if (modele == null || !modele.actif) return "redirect:/chat"

        val maintenant = LocalDateTime.now()
        val conversation = conversationDAO.save(
            Conversation(
                titre = titre.trim().ifEmpty { titreParDefaut },
                dateCreation = maintenant,
                dateDerniereModification = maintenant,
                modele = modele
            )
        )
        return "redirect:/chat?id=${conversation.id}"
    }

    // ----- Envoyer un message -----

    @PostMapping("/chat/{id}/message")
    fun envoyerMessage(
        @PathVariable id: Long,
        @RequestParam(defaultValue = "") texte: String,
        session: HttpSession
    ): String {
        if (!estConnecte(session)) return "redirect:/connexion"

        val conversation = conversationDAO.findById(id).orElse(null)
            ?: return "redirect:/chat"

        val prompt = texte.trim()
        if (prompt.isEmpty()) return "redirect:/chat?id=$id"

        // Sans titre saisi, le titre vient du premier message (40 caractères)
        if (conversation.titre == titreParDefaut && conversation.messages.isEmpty()) {
            conversation.titre = prompt.take(40)
        }

        // 1. On enregistre le message de l'élève
        messageDAO.save(
            Message(
                contenu = prompt,
                dateCreation = LocalDateTime.now(),
                typeMessage = TypeMessage.UTILISATEUR,
                conversation = conversation
            )
        )

        // 2. On interroge l'IA du modèle de la conversation
        val reponse = try {
            iaServiceFactory
                .getService(conversation.modele)
                .envoyerPrompt(prompt, conversation.modele)
        } catch (e: Exception) {
            logger.error("Erreur lors de l'appel à l'IA", e)
            "Désolé, le modèle n'a pas pu répondre pour le moment."
        }

        // 3. On enregistre la réponse
        messageDAO.save(
            Message(
                contenu = reponse,
                dateCreation = LocalDateTime.now(),
                typeMessage = TypeMessage.ASSISTANT,
                conversation = conversation
            )
        )

        // 4. La conversation remonte en haut de la liste
        conversation.dateDerniereModification = LocalDateTime.now()
        conversationDAO.save(conversation)

        return "redirect:/chat?id=$id"
    }

    // ----- Supprimer une conversation -----

    @PostMapping("/chat/{id}/supprimer")
    fun supprimerConversation(@PathVariable id: Long, session: HttpSession): String {
        if (!estConnecte(session)) return "redirect:/connexion"
        conversationDAO.deleteById(id)   // ses messages sont supprimés avec elle
        return "redirect:/chat"
    }
}