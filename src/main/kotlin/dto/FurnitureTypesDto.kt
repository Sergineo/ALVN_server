package com.hornet.dto

import kotlinx.serialization.Serializable

@Serializable
data class FurnitureTypesDto(
    val id_type: Int = 0,
    val furniture_type: String = "",
    val image: com.hornet.service.ImagePayload? = null
)