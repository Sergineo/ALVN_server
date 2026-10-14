package com.hornet.model.db

import org.jetbrains.exposed.sql.Table

object FurnitureType: Table("furniture_type") {
    val id_type = integer("id_type").autoIncrement()
    override val primaryKey = PrimaryKey(id_type)
    val furniture_type = varchar("furniture_type", 60)
    val image_name = varchar("image_name", 255).nullable()
}
