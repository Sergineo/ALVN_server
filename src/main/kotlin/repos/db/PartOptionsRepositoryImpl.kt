package com.hornet.repos.db

import com.hornet.model.db.PartOptions
import com.hornet.model.db.PartWeight
import com.hornet.service.DbRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class PartOptionsRepositoryImpl: DbRepository {
    override fun getAnyIdByAnyName(query: String?): Int? {
        return if(query != null) transaction {
            PartOptions.slice(PartOptions.id_option).selectAll().adjustWhere {
                PartOptions.option_name eq query
            }.singleOrNull()?.get(PartOptions.id_option)
        } else { null }
    }

    override fun selectAllAnyTable() = transaction { PartOptions.selectAll().toList() }
}