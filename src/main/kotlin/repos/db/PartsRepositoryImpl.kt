package com.hornet.repos.db

import com.hornet.dto.PartsDto
import com.hornet.mappers.rowToParts
import com.hornet.model.db.Parts
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class PartsRepositoryImpl {
    fun getPartsByIds(ids: List<Int>): List<PartsDto> {
        val result = transaction {
            Parts.selectAll().adjustWhere {
                Parts.id_part inList ids
            }.map { it.rowToParts() }
        }
        return result
    }

    fun getPartsIdsByPartsNames(queryList: List<String>?): Map<String, Int>? {
        return if (queryList != null) transaction {
            Parts.slice(Parts.id_part, Parts.article)
                .selectAll()
                .adjustWhere { Parts.article inList queryList }
                .associate { row -> row[Parts.article] to row[Parts.id_part] }
        }.ifEmpty { null } else null
    }

//    override fun getAnyIdByAnyName(query: String?): Int?{
//        return if(query != null) transaction {
//            Parts.slice(Parts.id_part).selectAll().adjustWhere {
//                Parts.part_name eq query
//            }
//        }.toString().toInt() else { null }
//    }
//    fun getImges
}