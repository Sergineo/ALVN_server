package com.hornet.mappers

import com.hornet.dto.PartOptionsDto
import com.hornet.model.db.PartOptions
import org.jetbrains.exposed.sql.ResultRow
import com.hornet.service.ImageLoader

fun ResultRow.toPartOptionDto() = PartOptionsDto(
    id_option = this[PartOptions.id_option],
    option_name = this[PartOptions.option_name],
    image = ImageLoader.load(this[PartOptions.image_name])
)
