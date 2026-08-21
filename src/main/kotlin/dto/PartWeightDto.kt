package com.hornet.dto

import kotlinx.serialization.Serializable

@Serializable
data class PartWeightDto(
    val id_weight: Int = 0,
    val part_weight: String = ""
)
