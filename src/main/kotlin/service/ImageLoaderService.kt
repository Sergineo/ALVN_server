package com.hornet.service

import com.hornet.error.exceptions.IncorrectImagePath

fun String?.prepareImageAnyResponse(): String? {
    val baseUrl = System.getenv("IMAGE_RESOURCE_ROUTE") ?: throw IncorrectImagePath()
    return if (this != null) "$baseUrl/$this"
    else null
}