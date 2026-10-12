package com.hornet.config

import com.hornet.service.ImageLoader
import io.ktor.server.application.*
import java.io.File

fun Application.configureImageLoader(){
    ImageLoader.imageDirectory = File(
        System.getProperty(
            environment.config.property("images_files.java_key").toString(),
            environment.config.property("images_files.path").toString()
        )
    ).canonicalFile
}