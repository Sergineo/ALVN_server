package com.hornet.config

import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

fun configureShema() {
    transaction {
        addLogger(StdOutSqlLogger)

        commit()
    }
}