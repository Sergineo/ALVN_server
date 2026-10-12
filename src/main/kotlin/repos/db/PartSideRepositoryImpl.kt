package com.hornet.repos.db

import com.hornet.mappers.toPartSideDto
import com.hornet.model.db.PartSide
import com.hornet.service.DbRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class PartSideRepositoryImpl : DbRepository {
    override fun getAnyIdByAnyName(query: String?): Int? {
        return if (query != null) transaction {
            PartSide.slice(PartSide.id_side).selectAll().adjustWhere {
                PartSide.part_side eq query
            }.singleOrNull()?.get(PartSide.id_side)
        } else {
            null
        }
    }

    override fun selectAllAnyTable() = transaction {
        PartSide.selectAll().toList().map { it.toPartSideDto() }
    }

    override fun checkNullableIdForExisting(id: Int?): Boolean {
        if (id == null) return true
        val result = transaction {
            PartSide.slice(PartSide.id_side).selectAll()
                .adjustWhere { PartSide.id_side eq id }
                .map { it[PartSide.id_side] }.firstOrNull()
        }
        return result != null
    }
}