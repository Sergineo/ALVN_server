package com.hornet.repos.db

import com.hornet.model.db.PartColors
import com.hornet.model.db.PartWeight
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

    override fun selectAllAnyTable() = transaction { PartColors.selectAll().toList() }
}