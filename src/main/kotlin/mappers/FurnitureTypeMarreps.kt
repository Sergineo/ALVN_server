package com.hornet.mappers

import com.hornet.dto.FurnitureTypesDto
import com.hornet.model.db.FurnitureType
import org.jetbrains.exposed.sql.ResultRow
import com.hornet.service.ImageLoader

fun ResultRow.toFurnitureTypeDto() = FurnitureTypesDto(
    id_type = this[FurnitureType.id_type],
    furniture_type = this[FurnitureType.furniture_type],
    image = ImageLoader.load(this[FurnitureType.image_name])
)
