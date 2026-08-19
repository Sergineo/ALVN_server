package com.hornet.mappers

import com.hornet.dto.SizesDto
import com.hornet.model.db.Sizes
import org.jetbrains.exposed.sql.ResultRow

fun String?.toIntSizeRange(): Pair<Int, Int>? {
    return if (this != null) Pair(
        this.split(" - ")[0].toInt(),
        this.split(" - ")[1].toInt()
    ) else {
        null
    }
}

fun Pair<Int, Int>?.toStringSizesRange(): String? {
    return if (this != null) "${this.first} - ${this.second}" else null
}

fun ResultRow.toSizesDto() = SizesDto(
    id_size = this[Sizes.id_size],
    max_width = this[Sizes.max_width],
    max_height = this[Sizes.max_height]
)