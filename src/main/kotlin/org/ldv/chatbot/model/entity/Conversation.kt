package org.ldv.chatbot.model.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import java.time.LocalDateTime

@Entity
class Conversation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var id: Long? = null,

    @Column(nullable = false)
    var titre: String,

    @Column(nullable = false)
    var dateCreation: LocalDateTime,

    @Column(nullable = false)
    var dateDerniereModification: LocalDateTime,

    // Le modèle choisi à la création : il ne change plus ensuite
    @ManyToOne
    @JoinColumn(name = "modele_id", nullable = false)
    var modele: ModeleIA
) {

    // Les messages de la conversation, du plus ancien au plus récent.
    // Supprimer la conversation supprime aussi ses messages.
    @OneToMany(mappedBy = "conversation", cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("dateCreation ASC")
    var messages: MutableList<Message> = mutableListOf()
}