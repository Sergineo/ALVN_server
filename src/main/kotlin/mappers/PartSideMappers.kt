package com.hornet.mappers

import com.hornet.dto.PartSideDto
import com.hornet.model.db.PartSide
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toPartSideDto() = PartSideDto(
    id_side = this[PartSide.id_side],
    part_side = this[PartSide.part_side]
)