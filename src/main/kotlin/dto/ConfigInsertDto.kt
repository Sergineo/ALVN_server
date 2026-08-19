package com.hornet.dto

data class ConfigInsertDto(
    val type: Int?,
    val size: Int?,
    val side: Int?,
    val weight: Int?,
    val color: Int?,
    val option: Int?,
    val configDto: ConfigDto
)
