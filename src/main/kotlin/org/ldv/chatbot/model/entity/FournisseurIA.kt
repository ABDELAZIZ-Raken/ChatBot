package org.ldv.chatbot.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class FournisseurIA(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var id: Long? = null,

    @Column(nullable = false)
    var nom: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    // Chemin du fichier du logo, facultatif
    var cheminLogo: String? = null
) {

    // Les modèles proposés par ce fournisseur
    @OneToMany(mappedBy = "fournisseur")
    var modeles: MutableList<ModeleIA> = mutableListOf()
}