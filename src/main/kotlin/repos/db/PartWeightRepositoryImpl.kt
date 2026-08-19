package com.hornet.repos.db

import com.hornet.model.db.PartSide
import com.hornet.model.db.PartWeight
import com.hornet.service.DbRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class PartWeightRepositoryImpl: DbRepository {
    override fun getAnyIdByAnyName(query: String?): Int? {
        return if(query != null) transaction {
            PartWeight.slice(PartWeight.id_weight).selectAll().adjustWhere {
                PartWeight.weight eq query
            }.singleOrNull()?.get(PartWeight.id_weight)
        } else { null }
    }

    override fun selectAllAnyTable() = transaction { PartWeight.selectAll().toList() }
}