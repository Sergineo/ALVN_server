package com.hornet.model

import org.jetbrains.exposed.sql.Table

object Product: Table() {
    val id_product = integer("id_product").autoIncrement()
    val product_mark = varchar("product_mark", 100)
    val product_name = varchar("product_name", 100)
    val product_category = varchar("product_category", 50)
    val product_units = varchar("product_units", 40)
    val product_capacity = integer("product_capacity")
    val product_file_image_name = varchar("product_file_image_name", 100)
}
