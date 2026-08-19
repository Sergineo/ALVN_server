package com.hornet.model.db

import com.hornet.model.db.FurnitureType.furniture_type
import com.hornet.model.db.FurnitureType.id_type
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object PartSide: Table("part_side") {
    val id_side = integer("id_side").autoIncrement()
    override val primaryKey = PrimaryKey(id_side)
    val part_side = varchar("part_side", 20)
}