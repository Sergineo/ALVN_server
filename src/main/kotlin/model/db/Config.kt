package com.hornet.model.db

import com.hornet.dto.ConfigParts
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.json.jsonb

object Config: Table(name = "config") {
    val id_config = integer("id_config").autoIncrement()
    val config_id_type = integer("config_id_type")
        .references(FurnitureType.id_type).nullable()
    val config_id_size = integer("config_id_size")
        .references(Sizes.id_size).nullable()
    val config_id_side = integer("config_id_side")
        .references(PartSide.id_side).nullable()
    val config_id_weight = integer("config_id_weight")
        .references(PartWeight.id_weight).nullable()
    val config_id_color = integer("config_id_color")
        .references(PartColors.id_color).nullable()
    val config_id_option = integer("config_id_option")
        .references(PartOptions.id_option).nullable()
    val config = jsonb<ConfigParts>("config", Json.Default)
}