package com.hornet.mappers

import com.hornet.dto.PartWeightDto
import com.hornet.model.db.PartSide.id_side
import com.hornet.model.db.PartSide.part_side
import com.hornet.model.db.PartWeight
import org.jetbrains.exposed.sql.ResultRow
import com.hornet.service.ImageLoader

fun ResultRow.toPartWeightDto() = PartWeightDto(
    id_weight = this[PartWeight.id_weight],
    part_weight = this[PartWeight.weight],
    image = ImageLoader.load(this[PartWeight.image_name])
)
