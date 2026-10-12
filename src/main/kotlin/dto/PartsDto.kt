package com.hornet.dto

import kotlinx.serialization.Serializable

@Serializable
data class PartsDto(
    val id: Int = 0,
    val part_name: String = "",
    val article: String = "",
    val quantity: Int = 0,
    val image: com.hornet.service.ImagePayload? = null
)