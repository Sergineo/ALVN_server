package com.hornet.controller

import com.hornet.mappers.toDto
import com.hornet.model.Product
import com.hornet.model.Product.product_category
import com.hornet.model.Product.product_file_image_name
import com.hornet.repos.MinioRepository
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction


fun Application.productRoutes(
    minioRepository: MinioRepository,
) {
    routing {
        get("/product/categories") {
            try {
                val categories = transaction {
                    Product
                        .slice(product_category)
                        .selectAll()
                        .withDistinct()
                        .map{ it[product_category] }
                }
                call.respond(listOf("Все") + categories)
            }catch (e:Exception){
                call.respondText(
                    e.toString(),
                    status = HttpStatusCode.InternalServerError,
                    contentType = ContentType.Text.Plain
                )
            }
        }
        get("/product/all") {
            try {
                val product = transaction {
                    Product
                        .selectAll()
                        .toList()
                }
                val result = product.map { p ->
                    val url = minioRepository.getUrlPicture(
                        objectName = p[product_file_image_name]
                    )
                    p.toDto(url)
                }
                call.respond(result)
            }catch (e:Exception){
                call.respondText(e.localizedMessage, ContentType.Text.Plain)
            }
        }
        get("/product/{category}") {
            val category = call.parameters["category"]
                ?: return@get call.respond(HttpStatusCode.BadRequest)
            try {
                val product = transaction {
                    Product
                        .select { product_category eq category }
                        .toList()
                }
                val result = product.map { p ->
                    val url = minioRepository.getUrlPicture(
                        objectName = p[product_file_image_name]
                    )
                    p.toDto(url)
                }
                call.respond(result)
            }catch (e:Exception){
                call.respondText(e.localizedMessage, ContentType.Text.Plain)
            }
        }
    }
}
