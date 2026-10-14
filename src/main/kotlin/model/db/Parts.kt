package com.hornet.model.db

import org.jetbrains.exposed.sql.Table

object Parts : Table(name = "parts") {
    val id_part = integer("id_part").autoIncrement()
    override val primaryKey = PrimaryKey(id_part)
    val part_name = varchar("part_name", 100)
    val article = varchar("article", 40)
    val image_name = varchar("image_name", 255).nullable()
}
