package com.hornet.repos.db

import com.hornet.mappers.toPartColorDto
import com.hornet.model.db.PartColors
import com.hornet.service.DbRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class PartColorsRepositoryImpl: DbRepository {
    override fun getAnyIdByAnyName(query: String?): Int? {
        return if(query != null) transaction {
            PartColors.slice(PartColors.id_color).selectAll().adjustWhere {
                PartColors.color_name eq query
            }.singleOrNull()?.get(PartColors.id_color)
        } else { null }
    }

    override fun selectAllAnyTable() = transaction {
        PartColors.selectAll().toList().map{ it.toPartColorDto() }
    }

    override fun checkNullableIdForExisting(id: Int?): Boolean {
        if (id == null) return true
        val result = transaction {
            PartColors.slice(PartColors.id_color).selectAll()
                .adjustWhere { PartColors.id_color eq id }
                .map { it[PartColors.id_color] }.firstOrNull()
        }
        return result != null
    }
}