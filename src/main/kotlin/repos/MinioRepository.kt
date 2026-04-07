package com.hornet.repos

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationEnvironment
import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.http.Method
import java.io.FileInputStream
import java.util.concurrent.TimeUnit

class MinioRepository(
    val minioClient: MinioClient,
    val environment: ApplicationEnvironment
)
{
    fun getUrlPicture(
        bucketName: String = environment.config.property("s3.bucket").getString(),
        objectName: String): String {
        return minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .`object`(objectName)
                .expiry(7, TimeUnit.DAYS)
                .build()
        )
    }
    fun loadPicture(
        path: String,
        bucketName: String = environment.config.property("s3.bucket").getString(),
        objectName: String) {
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .`object`(objectName)
                .stream(FileInputStream(path), -1, 10485760)
                .contentType("image/png")
                .build()
        )
    }
}