package com.hornet.repos

import org.jetbrains.exposed.sql.Database
import java.sql.DriverManager

class ProductRepository(
    val postgresConfig: DriverManager
) {

}