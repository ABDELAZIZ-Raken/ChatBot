package org.ldv.chatbot.service

import org.ldv.chatbot.config.IAService
import org.ldv.chatbot.dto.gemini.GeminiChatRequest
import org.ldv.chatbot.dto.gemini.GeminiChatResponse
import org.ldv.chatbot.dto.gemini.GeminiContent
import org.ldv.chatbot.dto.gemini.GeminiPart
import org.ldv.chatbot.model.entity.ModeleIA
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class GeminiService(
    private val restClientBuilder: RestClient.Builder,

    @Value("\${gemini.api.key}")
    private val apiKey: String
) : IAService {

    override fun getFournisseur(): String = "Gemini"

    override fun envoyerPrompt(prompt: String, modele: ModeleIA): String {

        // Étape 1 : le message de l'utilisateur
        val requete = GeminiChatRequest(
            contents = listOf(
                GeminiContent(
                    role = "user",
                    parts = listOf(GeminiPart(text = prompt))
                )
            )
        )

        // Étape 2 : envoi de la requête POST (la clé passe dans l'en-tête, pas dans l'URL)
        val reponse = restClientBuilder.build()
            .post()
            .uri(modele.urlApi)
            .header("x-goog-api-key", apiKey.trim())
            .contentType(MediaType.APPLICATION_JSON)
            .body(requete)
            .retrieve()
            .body(GeminiChatResponse::class.java)

        // Étape 3 : candidates[0].content.parts[0].text
        return reponse?.candidates?.firstOrNull()
            ?.content?.parts?.firstOrNull()?.text
            ?: "Aucune réponse reçue de Gemini."
    }
}