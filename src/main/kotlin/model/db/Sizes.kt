package com.hornet.model.db

import org.jetbrains.exposed.sql.Table

object Sizes: Table("sizes") {
    val id_size = integer("id_size").autoIncrement()
    override val primaryKey = PrimaryKey(id_size)
    val max_height = integer("max_height")
    val max_width = integer("max_width")
}