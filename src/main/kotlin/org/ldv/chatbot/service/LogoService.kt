package org.ldv.chatbot.service

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID

@Service
class LogoService {

    // Dossier où sont enregistrés les logos envoyés
    private val dossier: Path = Paths.get("uploads", "logos")

    // Début du chemin des logos envoyés (voir WebConfig)
    private val prefixe = "/logos/"

    // Types d'image acceptés → extension du fichier
    private val extensions = mapOf(
        "image/png" to "png",
        "image/jpeg" to "jpg",
        "image/webp" to "webp"
    )

    init {
        Files.createDirectories(dossier)
    }

    // Enregistre le fichier et retourne son chemin (ex. « /logos/3f2a….png »),
    // ou null si absent / format refusé
    fun enregistrer(fichier: MultipartFile?): String? {
        if (fichier == null || fichier.isEmpty) return null
        val extension = extensions[fichier.contentType] ?: return null

        // Nom généré : on n'utilise jamais le nom envoyé par l'utilisateur
        val nom = UUID.randomUUID().toString() + "." + extension
        fichier.transferTo(dossier.resolve(nom))
        return prefixe + nom
    }

    // Supprime un logo envoyé (les autres chemins, comme /images/..., sont ignorés)
    fun supprimer(chemin: String?) {
        if (chemin != null && chemin.startsWith(prefixe)) {
            Files.deleteIfExists(dossier.resolve(chemin.removePrefix(prefixe)))
        }
    }
}