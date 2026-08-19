package com.hornet.repos.db

import com.hornet.model.db.FurnitureType
import com.hornet.model.db.Parts
import com.hornet.service.DbRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class FurnitureTypeRepositoryImpl: DbRepository {
    override fun getAnyIdByAnyName(query: String?): Int?{
        return if(query != null) transaction {
            FurnitureType.slice(FurnitureType.id_type).selectAll().adjustWhere {
                FurnitureType.furniture_type eq query
            }.singleOrNull()?.get(FurnitureType.id_type)
        } else { null }
    }

    override fun selectAllAnyTable() = transaction { FurnitureType.selectAll().toList() }
}