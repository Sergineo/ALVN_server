package com.hornet.mappers

import com.hornet.dto.ConfigDto
import com.hornet.dto.Item
import com.hornet.model.db.Config
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.queryToConfig(): ConfigDto {
    return ConfigDto(config = this[Config.config])
}

fun getPairOfConfig(lst: List<ConfigDto>): Pair<List<Item>?, Map<Int, Item>?> {
    val baseAndAdd = lst.flatMap { (it.config.basePart).orEmpty() + (it.config.addParts).orEmpty() }
        .takeIf { it.isNotEmpty() }

    val deletes = lst.flatMap { (it.config.deleteParts).orEmpty() }
        .takeIf { it.isNotEmpty() }
        ?.associateBy { it.id }

    return Pair(baseAndAdd, deletes)
}