package com.hornet.repos.db

import com.hornet.dto.SizesDto
import com.hornet.mappers.toIntSizeRange
import com.hornet.mappers.toSizesDto
import com.hornet.model.db.Config
import com.hornet.model.db.Sizes
import com.hornet.service.DbRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class SizesRepositoryImpl : DbRepository {
    override fun getAnyIdByAnyName(query: String?): Int? {
        val range = query?.toIntSizeRange()
        return if (range != null) transaction {
            val maxWidth = range.first
            val maxHeight = range.second
            Sizes.slice(Sizes.id_size).selectAll().adjustWhere {
                Sizes.max_width eq maxWidth and (Sizes.max_height eq maxHeight)
            }.singleOrNull()?.get(Sizes.id_size)
        } else {
            null
        }
    }

    override fun selectAllAnyTable() = transaction {
        Sizes.selectAll().toList().map{ it.toSizesDto() }
    }

    fun getSizesRangeByFurnitureTypeId(typeId: Int): List<SizesDto> {
        return transaction {
            (Sizes innerJoin Config).slice(
                Sizes.id_size,
                Sizes.max_width,
                Sizes.max_height,
                Sizes.image_name,
                Config.config_id_type
            ).selectAll().adjustWhere {
                Config.config_id_type eq typeId
            }.toList().map{ it.toSizesDto() }
        }
    }

    override fun checkNullableIdForExisting(id: Int?): Boolean {
        if (id == null) return true
        val result = transaction {
            Sizes.slice(Sizes.id_size).selectAll()
                .adjustWhere { Sizes.id_size eq id }
                .map { it[Sizes.id_size] }.firstOrNull()
        }
        return result != null
    }
}