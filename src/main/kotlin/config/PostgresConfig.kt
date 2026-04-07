package com.hornet.config


import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import java.sql.Connection
import java.sql.DriverManager

fun Application.connectToPostgres(
    url: String = environment.config.property("postgres.url").getString(),
    driver: String = environment.config.property("postgres.driver").getString(),
    user: String = environment.config.property("postgres.user").getString(),
    password: String = environment.config.property("postgres.password").getString()
){
    Database.connect(url, driver, user, password)
}