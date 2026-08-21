package com.hornet.mappers

import com.hornet.dto.PartOptionsDto
import com.hornet.model.db.PartOptions
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toPartOptionDto() = PartOptionsDto(
    id_option = this[PartOptions.id_option],
    option_name = this[PartOptions.option_name]

)