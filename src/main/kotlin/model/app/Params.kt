package com.hornet.model.app

data class Params(
    val config_id_type: Int?,
    val config_id_size: Int?,
    val config_id_side: Int?,
    val config_id_weight: Int?,
    val config_id_color: Int?,
    val config_id_option: List<Int> = emptyList()
)
