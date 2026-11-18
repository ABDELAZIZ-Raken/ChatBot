package org.ldv.chatbot.service

import org.ldv.chatbot.model.ModeleIA
import org.springframework.stereotype.Service

@Service
class ModeleService {

    // Modèles de la maquette
    private val modeles = mutableListOf(
        ModeleIA(1, "Mistral Small 4", "Mistral", "mistral-small-4",
            "https://api.mistral.ai/v1/chat/completions",
            "Modèle rapide et économique, adapté aux questions courantes, à la reformulation et à l’aide au code.", true),
        ModeleIA(2, "Claude Haiku 4.5", "Anthropic", "claude-haiku-4-5",
            "https://api.anthropic.com/v1/messages",
            "Réponses claires et structurées, bon pour rédiger et expliquer.", true),
        ModeleIA(3, "GPT-6.1 Luna", "OpenAI", "gpt-6.1-luna",
            "https://api.openai.com/v1/chat/completions",
            "Modèle très économique pour les usages courants.", false),
        ModeleIA(4, "Gemini 2.5 Flash-Lite", "Google", "gemini-2.5-flash-lite",
            "https://generativelanguage.googleapis.com/v1beta/models",
            "Très rapide, adapté aux réponses courtes.", true)
    )

    private var prochainId = 5

    fun tous(): List<ModeleIA> = modeles.toList()

    // Les modèles visibles par les élèves (accueil et nouvelle conversation)
    fun actifs(): List<ModeleIA> = modeles.filter { it.actif }

    fun trouver(id: Int): ModeleIA? = modeles.firstOrNull { it.id == id }

    fun creer(nom: String, fournisseur: String, identifiant: String,
              urlApi: String, description: String, actif: Boolean) {
        modeles.add(
            ModeleIA(prochainId++, nom.trim(), fournisseur, identifiant.trim(),
                urlApi.trim(), description.trim(), actif)
        )
    }

    fun modifier(id: Int, nom: String, fournisseur: String, identifiant: String,
                 urlApi: String, description: String, actif: Boolean) {
        val m = trouver(id) ?: return
        m.nom = nom.trim()
        m.fournisseur = fournisseur
        m.identifiant = identifiant.trim()
        m.urlApi = urlApi.trim()
        m.description = description.trim()
        m.actif = actif
    }

    // Active ou désactive un modèle
    fun basculer(id: Int) {
        val m = trouver(id) ?: return
        m.actif = !m.actif
    }

    fun supprimer(id: Int) {
        modeles.removeIf { it.id == id }
    }
}