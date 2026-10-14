package com.hornet.mappers

import com.hornet.dto.FurnitureTypesDto
import com.hornet.model.db.FurnitureType
import com.hornet.service.prepareImageAnyResponse
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toFurnitureTypeDto() = FurnitureTypesDto(
    id_type = this[FurnitureType.id_type],
    furniture_type = this[FurnitureType.furniture_type],
    image_name = this[FurnitureType.image_name].prepareImageAnyResponse()
)