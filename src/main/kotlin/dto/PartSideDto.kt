package com.hornet.dto

import kotlinx.serialization.Serializable

@Serializable
data class PartSideDto(
    val id_side: Int = 0,
    val part_side: String = ""
)