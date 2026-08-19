package com.hornet.config

import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Application.swaggerConfig(){
    routing {
        swaggerUI(path = "/docs") {

        }
    }
}