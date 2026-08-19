package com.hornet.repos.db

import com.hornet.mappers.toIntSizeRange
import com.hornet.model.db.PartSide
import com.hornet.model.db.Parts
import com.hornet.model.db.Sizes
import com.hornet.service.DbRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class PartSideRepositoryImpl: DbRepository {
    override fun getAnyIdByAnyName(query: String?): Int? {
        return if(query != null) transaction {
            PartSide.slice(PartSide.id_side).selectAll().adjustWhere {
                PartSide.part_side eq query
            }.singleOrNull()?.get(PartSide.id_side)
        } else { null }
    }

    override fun selectAllAnyTable() = transaction { PartSide.selectAll().toList() }
}