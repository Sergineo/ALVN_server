package com.hornet.config

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/** Serves images at /images/<filename>. IMAGE_DIRECTORY is the filesystem directory. */
fun Application.configureStaticRouting(
) {
    val imageRoute: String by lazy { environment.config.property("static.images_route").getString() }
    routing {
        get("$imageRoute/{name}") {
            val name = call.parameters["name"]
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            if (name.contains("..") || name.contains('/') || name.contains('\\')) {
                return@get call.respond(HttpStatusCode.BadRequest)
            }

            val bytes = Thread.currentThread().contextClassLoader
                .getResourceAsStream("images/$name")
                ?.use { it.readBytes() }
                ?: return@get call.respond(HttpStatusCode.NotFound)

            val ct = when (name.substringAfterLast('.', "").lowercase()) {
                "png" -> ContentType.Image.PNG
                "xml" -> ContentType.Application.Xml.withCharset(Charsets.UTF_8)
                "jpg", "jpeg" -> ContentType.Image.JPEG
                "gif" -> ContentType.Image.GIF
                "webp" -> ContentType.parse("image/webp")
                "bmp" -> ContentType.parse("image/bmp")
                else -> ContentType.Application.OctetStream
            }

            call.respondBytes(bytes, ct)
        }
    }
}
