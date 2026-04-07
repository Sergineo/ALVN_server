package com.hornet.controller

import com.hornet.model.Product
import com.hornet.repos.MinioRepository
import io.ktor.server.application.Application
import io.minio.MinioClient
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class ProductAdder(
    val minioRepository: MinioRepository,
) {
    fun addProduct(product: Product, path: String) {
        transaction {
            Product.insert {
                it[product_mark] = product.product_mark
                it[product_name] = product.product_name
                it[product_category] = product.product_category
                it[product_units] = product.product_units
                it[product_capacity] = product.product_capacity
                it[product_file_image_name] = product.product_file_image_name
            }
        }
        minioRepository.loadPicture(
            path,
            objectName = product.product_file_image_name.toString()
        )
    }
}