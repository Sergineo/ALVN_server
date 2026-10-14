package com.hornet.mappers

import com.hornet.dto.PartWeightDto
import com.hornet.model.db.PartWeight
import com.hornet.service.prepareImageAnyResponse
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toPartWeightDto() = PartWeightDto(
    id_weight = this[PartWeight.id_weight],
    part_weight = this[PartWeight.weight],
    image_name = this[PartWeight.image_name].prepareImageAnyResponse()
)