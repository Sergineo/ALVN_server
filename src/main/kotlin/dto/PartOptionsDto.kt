package com.hornet.dto

import kotlinx.serialization.Serializable

@Serializable
data class PartOptionsDto(
    val id_option: Int = 0,
    val option_name: String = ""
)