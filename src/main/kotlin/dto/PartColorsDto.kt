package com.hornet.dto

import kotlinx.serialization.Serializable

@Serializable
data class PartColorsDto(
    val id_color: Int,
    val color_name: String,
    val hex_code: String? = null,
    val image: com.hornet.service.ImagePayload? = null
)