package com.hornet.config

import io.ktor.server.application.Application
import io.minio.MinioClient


fun Application.connectToS3(
    baseUrl: String = environment.config.property("s3.baseurl").getString(),
    accessKey: String = environment.config.property("s3.username").getString(),
    secretKey: String = environment.config.property("s3.password").getString(),
): MinioClient {
    return MinioClient.builder()
        .endpoint(baseUrl)
        .credentials(accessKey, secretKey)
        .build()
}