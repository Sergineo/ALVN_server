package com.hornet.model.db

import org.jetbrains.exposed.sql.Table

object PartColors: Table("part_colors"){
    val id_color = integer("id_color").autoIncrement()
    override val primaryKey = PrimaryKey(id_color)
    val color_name = varchar("color_name", 60)
    val hex_code = varchar("hex_code", 7)
}