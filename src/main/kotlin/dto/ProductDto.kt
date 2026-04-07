package com.hornet.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id_product: Int,
    val product_mark: String,
    val product_name: String,
    val product_category: String,
    val product_units: String,
    val product_capacity: Int,
    val product_image_link: String,
)