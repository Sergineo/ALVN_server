package com.hornet.model.db

import org.jetbrains.exposed.sql.Table

object PartOptions: Table("part_options") {
    val id_option = integer("id_option").autoIncrement()
    override val primaryKey = PrimaryKey(id_option)
    val option_name = varchar("option_name", 100)
    val image_name = varchar("image_name", 255).nullable()
}
