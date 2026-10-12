package com.hornet.mappers

import com.hornet.dto.Item
import com.hornet.dto.PartsDto
import com.hornet.model.db.Parts
import org.jetbrains.exposed.sql.ResultRow
import com.hornet.service.ImageLoader

fun ResultRow.rowToParts() = PartsDto(
    id = this[Parts.id_part],
    part_name = this[Parts.part_name],
    article = this[Parts.article],
    image = ImageLoader.load(this[Parts.image_name])
)

fun PartsDto.toUiConfig(quantity: Int) = PartsDto(
    id = this.id,
    part_name = this.part_name,
    article = this.article,
    quantity = quantity,
    image = this.image,
)

fun List<PartsDto>.listToUiConfigList(resultList: List<Item>): List<PartsDto> {
    val dbPartsMap = this.associateBy { it.id }
    return resultList.mapNotNull { item ->
        val dbPartDto = dbPartsMap[item.id]

        dbPartDto?.toUiConfig(item.q)
    }
}
