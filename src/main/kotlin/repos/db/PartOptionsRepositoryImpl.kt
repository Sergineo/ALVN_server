package com.hornet.repos.db

import com.hornet.mappers.toPartOptionDto
import com.hornet.model.db.PartOptions
import com.hornet.service.DbRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class PartOptionsRepositoryImpl : DbRepository {
    override fun getAnyIdByAnyName(query: String?): Int? {
        return if (query != null) transaction {
            PartOptions.slice(PartOptions.id_option).selectAll().adjustWhere {
                PartOptions.option_name eq query
            }.singleOrNull()?.get(PartOptions.id_option)
        } else {
            null
        }
    }

    override fun selectAllAnyTable() =
        transaction {
            PartOptions.selectAll().toList().map { it.toPartOptionDto() }
        }

    override fun checkNullableIdForExisting(id: Int?): Boolean {
        if (id == null) return true
        val result = transaction {
            PartOptions.slice(PartOptions.id_option).selectAll()
                .adjustWhere { PartOptions.id_option eq id }
                .map { it[PartOptions.id_option] }.firstOrNull()
        }
        return result != null
    }
    fun checkIdForExisting(id: Int): Boolean {
        val result = transaction {
            PartOptions.slice(PartOptions.id_option).selectAll()
                .adjustWhere { PartOptions.id_option eq id }
                .map { it[PartOptions.id_option] }.firstOrNull()
        }
        return result != null
    }
}