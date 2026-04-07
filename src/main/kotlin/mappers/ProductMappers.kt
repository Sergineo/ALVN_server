package com.hornet.mappers

import com.hornet.dto.ProductDto
import com.hornet.model.Product
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toDto(url: String) = ProductDto(
    id_product = this[Product.id_product],
    product_mark = this[Product.product_mark],
    product_name = this[Product.product_name],
    product_category = this[Product.product_category],
    product_units = this[Product.product_units],
    product_capacity = this[Product.product_capacity],
    product_image_link = url
)