package com.hornet.controller

import com.hornet.service.ConfigService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


//fun Application.furnitureRoutes(
//    configService: ConfigService
//) {
//    routing {
//        get("/furniture/types") {
//            try {
//                val response = configService.getConfig()
//                call.respond(HttpStatusCode.OK, response)
//            }catch (e:Exception){
//                call.respondText(
//                    e.toString(),
//                    status = HttpStatusCode.InternalServerError,
//                    contentType = ContentType.Text.Plain
//                )
//            }
//        }
//        get("/furniture/types") {
//            try {
//
//            }catch (e:Exception){
//                call.respondText(
//                    e.toString(),
//                    status = HttpStatusCode.InternalServerError,
//                    contentType = ContentType.Text.Plain
//                )
//            }
//        }
//    }
//}
