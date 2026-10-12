package com.hornet.controller

import com.hornet.error.error_handling.ApiRoutingExceptionsHandlingImpl
import com.hornet.model.app.Params
import com.hornet.service.ConfigService
import com.hornet.service.RepositoryInjection
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.furnitureRoutes(
    repository: RepositoryInjection,
    configService: ConfigService,
    apiRoutingExceptionsHandlingImpl: ApiRoutingExceptionsHandlingImpl = ApiRoutingExceptionsHandlingImpl()
) {
    routing {
        get("/furniture/types") {
            try {
                val response = repository.furnitureTypeRepositoryImpl.selectAllAnyTable()
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                call.respondText(
                    e.message.toString(),
                    status = HttpStatusCode.InternalServerError,
                    contentType = ContentType.Text.Plain
                )
            }
        }
        get("/furniture/sizes/{id_type}") {
            try {
                val idType = call.parameters["id_type"]?.toIntOrNull()
                val response = apiRoutingExceptionsHandlingImpl.checkFormatIdForWeightRequest(
                    id = idType,
                    callback = { id ->
                        repository.sizesRepositoryImpl.getSizesRangeByFurnitureTypeId(id)
                    }
                )
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                call.respondText(
                    e.message.toString(),
                    status = HttpStatusCode.InternalServerError,
                    contentType = ContentType.Text.Plain
                )
            }

        }
        get("/furniture/sides") {
            try {
                val response = repository.partSideRepositoryImpl.selectAllAnyTable()
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                call.respondText(
                    e.message.toString(),
                    status = HttpStatusCode.InternalServerError,
                    contentType = ContentType.Text.Plain
                )
            }
        }
        get("/furniture/weight") {
            try {
                val response = repository.partWeightRepositoryImpl.selectAllAnyTable()
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                call.respondText(
                    e.message.toString(),
                    status = HttpStatusCode.InternalServerError,
                    contentType = ContentType.Text.Plain
                )
            }
        }
        get("/furniture/colors") {
            try {
                val response = repository.partColorsRepositoryImpl.selectAllAnyTable()
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                call.respondText(
                    e.message.toString(),
                    status = HttpStatusCode.InternalServerError,
                    contentType = ContentType.Text.Plain
                )
            }
        }
        get("/furniture/options") {
            try {
                val configIdType = call.parameters["config_id_type"]?.toIntOrNull()
                val configIdSize = call.parameters["config_id_size"]?.toIntOrNull()
                val configIdSide = call.parameters["config_id_side"]?.toIntOrNull()
                val configIdWeight = call.parameters["config_id_weight"]?.toIntOrNull()
                val configIdColor = call.parameters["config_id_color"]?.toIntOrNull()

                val configIdOption = call.parameters.getAll("config_id_option")
                    ?.mapNotNull { it.toIntOrNull() }
                    ?: emptyList()

                val params = Params(
                    config_id_type =
                        apiRoutingExceptionsHandlingImpl.checkFormatNullableIdForConfigRequest(
                            id = configIdType,
                            callback = { id ->
                                repository.furnitureTypeRepositoryImpl.checkNullableIdForExisting(id)
                            }
                        ),
                    config_id_size =
                        apiRoutingExceptionsHandlingImpl.checkFormatNullableIdForConfigRequest(
                            id = configIdSize,
                            callback = { id ->
                                repository.sizesRepositoryImpl.checkNullableIdForExisting(id)
                            }
                        ),
                    config_id_side =
                        apiRoutingExceptionsHandlingImpl.checkFormatNullableIdForConfigRequest(
                            id = configIdSide,
                            callback = { id ->
                                repository.partSideRepositoryImpl.checkNullableIdForExisting(id)
                            }
                        ),
                    config_id_weight = apiRoutingExceptionsHandlingImpl.checkFormatNullableIdForConfigRequest(
                        id = configIdWeight,
                        callback = { id ->
                            repository.partWeightRepositoryImpl.checkNullableIdForExisting(id)
                        }
                    ),
                    config_id_color = apiRoutingExceptionsHandlingImpl.checkFormatNullableIdForConfigRequest(
                        id = configIdColor,
                        callback = { id ->
                            repository.partColorsRepositoryImpl.checkNullableIdForExisting(id)
                        }
                    ),
                    config_id_option = configIdOption.map {
                        apiRoutingExceptionsHandlingImpl.checkFormatIdForConfigRequest(
                            id = it,
                            callback = { id ->
                                repository.partOptionsRepositoryImpl.checkIdForExisting(id)
                            }
                        )
                    }
                )

                val response = configService.getConfig(params)

                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                call.respondText(
                    e.message.toString(),
                    status = HttpStatusCode.InternalServerError,
                    contentType = ContentType.Text.Plain
                )
            }
        }
    }
}
