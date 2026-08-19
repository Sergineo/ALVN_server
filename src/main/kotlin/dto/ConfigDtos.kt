package com.hornet.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConfigDto(
    val config: ConfigParts
) {
    fun cq() {
        TODO("Not yet implemented")
    }
}

@Serializable
data class ConfigParts(
    val basePart: List<Item>? = emptyList(),
    val addParts: List<Item>? = emptyList(),
    val deleteParts: List<Item>? = emptyList(),
)

@Serializable
data class Item constructor(
    val id: Int,
    var q: Int,
)