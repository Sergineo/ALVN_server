package com.hornet.service

import com.hornet.error.exceptions.UnextensibleImageFormat
import kotlinx.serialization.Serializable
import java.io.File
import java.util.Base64

/**
 * Единый формат передачи изображения через JSON.
 * Для растровых файлов data содержит Base64 исходных байтов (клиент декодирует их в Bitmap).
 * Для XML data содержит текст XML.
 */
@Serializable
data class ImagePayload(
    val fileName: String,
    val extension: String,
    val kind: String,
    val data: String
)

object ImageLoader {
    lateinit var imageDirectory: File

    fun load(fileName: String?): ImagePayload? {
        if (fileName.isNullOrBlank()) return null

        val file = File(imageDirectory, fileName).canonicalFile
        require(file.toPath().startsWith(imageDirectory.toPath())) {
            "Недопустимый путь к изображению: $fileName"
        }
        if (!file.isFile) return null

        val extension = file.name.substringAfterLast('.', "").lowercase()
        return when (extension) {
            "png", "jpg", "jpeg", "gif", "bmp", "webp" -> ImagePayload(
                fileName = file.name,
                extension = extension,
                kind = "bitmap",
                data = Base64.getEncoder().encodeToString(file.readBytes())
            )
            "xml" -> ImagePayload(
                fileName = file.name,
                extension = extension,
                kind = "xml",
                data = file.readText(Charsets.UTF_8)
            )
            else -> throw UnextensibleImageFormat(extension)
        }
    }
}
