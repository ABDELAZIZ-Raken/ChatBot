package org.ldv.chatbot.service

import org.ldv.chatbot.config.IAService
import org.ldv.chatbot.model.entity.ModeleIA
import org.springframework.stereotype.Component

@Component
class IAServiceFactory(

    // Spring fournit ici tous les services qui implémentent IAService
    // (MistralService, GeminiService…)
    private val services: List<IAService>

) {

    fun getService(modele: ModeleIA): IAService {

        val fournisseur = modele.fournisseur.nom

        // Sélection du service correspondant au fournisseur
        return services.firstOrNull {
            it.getFournisseur().equals(fournisseur, ignoreCase = true)
        } ?: throw IllegalArgumentException(
            "Aucun service disponible pour $fournisseur"
        )
    }
}