package com.hornet.mappers

import com.hornet.dto.PartColorsDto
import com.hornet.model.db.PartColors
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toPartColorDto() = PartColorsDto(
    id_color = this[PartColors.id_color],
    color_name = this[PartColors.color_name],
    hex_code = this[PartColors.hex_code]
)
