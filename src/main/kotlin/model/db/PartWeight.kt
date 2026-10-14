package com.hornet.model.db

import com.hornet.model.db.FurnitureType.furniture_type
import com.hornet.model.db.FurnitureType.id_type
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object PartWeight: Table("part_weight") {
    val id_weight = integer("id_weight").autoIncrement()
    override val primaryKey: PrimaryKey = PrimaryKey(id_weight)
    val weight = varchar("part_weight", 60)
    val image_name = varchar("image_name", 255).nullable()
}
