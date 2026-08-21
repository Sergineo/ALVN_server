package com.hornet.repos.db

import com.hornet.dto.ConfigInsertDto
import com.hornet.dto.Item
import com.hornet.mappers.getPairOfConfig
import com.hornet.mappers.queryToConfig
import com.hornet.model.app.Params
import com.hornet.model.db.Config
import com.hornet.service.DbRepository
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ConfigRepositoryImpl {
    fun getConfigurationIds(
        params: Params
    ): Pair<List<Item>?, Map<Int, Item>?> {
        val configDto = transaction {
            Config.slice(listOf(Config.config)).select {
                (
                    (
                        (Config.config_id_type eq params.config_id_type) or (Config.config_id_type.isNull())) and
                        ((Config.config_id_size eq params.config_id_size) or (Config.config_id_size.isNull())) and
                        ((Config.config_id_side eq params.config_id_side) or (Config.config_id_side.isNull())) and
                        ((Config.config_id_weight eq params.config_id_weight) or (Config.config_id_weight.isNull())) and
                        ((Config.config_id_color eq params.config_id_color) or (Config.config_id_color.isNull()))
                    ) or (
                        Config.config_id_option inList params.config_id_option
                    )
            }.map { it.queryToConfig() }
        }

        return getPairOfConfig(configDto)
    }

    fun insertConfigInDb(configDto: List<ConfigInsertDto>) {
        return if(configDto.isNotEmpty()) transaction {
            Config.deleteAll()
            Config.batchInsert(configDto) { dto ->
                this[Config.config_id_type] = dto.type
                this[Config.config_id_size] = dto.size
                this[Config.config_id_side] = dto.side
                this[Config.config_id_weight] = dto.weight
                this[Config.config_id_color] = dto.color
                this[Config.config_id_option] = dto.option
                this[Config.config] = dto.configDto.config
            }
        } else {}
    }
}