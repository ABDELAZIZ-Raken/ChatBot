package org.ldv.chatbot.service

import org.ldv.chatbot.model.Compte
import org.springframework.stereotype.Service

@Service
class CompteService {

    // Comptes de démonstration de la maquette
    private val comptes = mutableListOf(
        Compte("mail@gmail.com", "123", "UTILISATEUR"),
        Compte("admin@gmail.com", "admin", "ADMIN")
    )

    fun tous(): List<Compte> = comptes.toList()

    // Retourne le compte si l'e-mail et le mot de passe sont corrects, sinon null
    fun authentifier(email: String, motDePasse: String): Compte? {
        return comptes.firstOrNull {
            it.email.equals(email.trim(), ignoreCase = true) && it.motDePasse == motDePasse
        }
    }

    fun existe(email: String): Boolean {
        return comptes.any { it.email.equals(email.trim(), ignoreCase = true) }
    }

    // Crée un compte utilisateur
    fun inscrire(email: String, motDePasse: String): Compte {
        val compte = Compte(email.trim().lowercase(), motDePasse, "UTILISATEUR")
        comptes.add(compte)
        return compte
    }

    // Supprime un compte utilisateur (on ne supprime jamais un administrateur)
    fun supprimer(email: String) {
        comptes.removeIf { it.email.equals(email, ignoreCase = true) && !it.estAdmin }
    }
}