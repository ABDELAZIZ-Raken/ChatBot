package org.ldv.chatbot.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

@Entity
class ModeleIA(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var id: Long? = null,

    @Column(nullable = false)
    var nom: String,

    // Identifiant technique utilisé par l'API (ex. « mistral-small-4 »)
    @Column(nullable = false)
    var identifiantTechnique: String,

    @Column(nullable = false)
    var urlApi: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    // Chemin du fichier du logo, facultatif
    var cheminLogo: String? = null,

    // Modèle proposé ou non aux utilisateurs (activer / désactiver dans l'administration)
    @Column(nullable = false)
    var actif: Boolean = true,

    // Plusieurs modèles appartiennent à un fournisseur
    @ManyToOne
    @JoinColumn(name = "fournisseur_id", nullable = false)
    var fournisseur: FournisseurIA
) {

    // Les conversations qui utilisent ce modèle
    @OneToMany(mappedBy = "modele")
    var conversations: MutableList<Conversation> = mutableListOf()

    // Initiales affichées dans le rond quand il n'y a pas de logo
    // (ex. « mistral-small-latest » → « M »)
    val initiales: String
        get() = nom.trim().split(Regex("[\\s\\-]+"))
            .mapNotNull { it.firstOrNull() }
            .joinToString("")
            .take(2)
            .uppercase()
}


