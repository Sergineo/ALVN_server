package com.hornet.dto

import kotlinx.serialization.Serializable

@Serializable
data class SizesDto(
    val id_size: Int = 0,
    val max_width: Int = 0,
    val max_height: Int = 0,
)
