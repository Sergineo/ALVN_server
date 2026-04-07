package com.hornet

import com.hornet.config.configureShema
import com.hornet.config.connectToPostgres
import com.hornet.config.connectToS3
import com.hornet.config.setTransferFormatData
import com.hornet.controller.ProductAdder
import com.hornet.controller.productRoutes
import com.hornet.repos.MinioRepository
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module()
{
    setTransferFormatData()
    connectToPostgres()
    configureShema()
    val minioConfig = connectToS3()
    val repository = MinioRepository(minioConfig, this.environment)
    productRoutes(repository)
}
