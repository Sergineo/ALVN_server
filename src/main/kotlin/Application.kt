package com.hornet

import com.hornet.config.configureImageLoader
import com.hornet.config.configureShema
import com.hornet.config.connectToPostgres
import com.hornet.config.setTransferFormatData
import com.hornet.config.swaggerConfig
import com.hornet.controller.furnitureRoutes
import com.hornet.dto.ConfigInsertDto
import com.hornet.dto.Item
import com.hornet.model.app.Params
import com.hornet.repos.db.PartColorsRepositoryImpl
import com.hornet.repos.db.ConfigRepositoryImpl
import com.hornet.repos.db.FurnitureTypeRepositoryImpl
import com.hornet.repos.db.PartOptionsRepositoryImpl
import com.hornet.repos.db.PartSideRepositoryImpl
import com.hornet.repos.db.PartWeightRepositoryImpl
import com.hornet.repos.db.PartsRepositoryImpl
import com.hornet.repos.db.RepositoryInjectionImpl
import com.hornet.repos.db.SizesRepositoryImpl
import com.hornet.service.ConfigService
import io.ktor.server.application.*
import io.ktor.server.netty.*
import kotlin.collections.forEachIndexed

fun main(args: Array<String>) { EngineMain.main(args) }

fun Application.module()
{
    setTransferFormatData()

    connectToPostgres()
    configureShema()

    swaggerConfig()

    configureImageLoader()

    val repository = RepositoryInjectionImpl(
        partsRepositoryImpl = PartsRepositoryImpl(),
        configRepositoryImpl = ConfigRepositoryImpl(),
        furnitureTypeRepositoryImpl = FurnitureTypeRepositoryImpl(),
        sizesRepositoryImpl = SizesRepositoryImpl(),
        partSideRepositoryImpl = PartSideRepositoryImpl(),
        partWeightRepositoryImpl = PartWeightRepositoryImpl(),
        partColorsRepositoryImpl = PartColorsRepositoryImpl(),
        partOptionsRepositoryImpl = PartOptionsRepositoryImpl()
    )
    val configService = ConfigService(repository)
//    print("Укажите путь вашего файла\n")
//    val path = """C:\Insert_pattern.xlsx"""
//    print("Укажите название листа\n")
//    val sheet = "Лист1"
//    val result = configService.insertNewConfigByCalibrationCell(path = path, sheetName = sheet)
//    if (result.isSuccess) {
//        printPrettyConfigList(result.getOrThrow())
//    }
//    val params = Params(
//        config_id_type = 3,
//        config_id_size = 10,
//        config_id_side = 2,
//        config_id_weight = 3,
//        config_id_color = 3,
//        config_id_option = emptyList()
//    )
//    val repoResult = repository.configRepositoryImpl.getConfigurationIds(params)
//    println(repoResult)
//    val result = configService.getConfig(params)
//    result.forEach {
//        println("${it.article}, ${it.quantity};")
//    }



    furnitureRoutes(repository, configService)

//    val minioConfig = connectToS3()
//    val repository = MinioRepository(minioConfig, this.environment)
//    productRoutes(repository)
}

fun printPrettyConfigList(configList: List<ConfigInsertDto>) {
    println("\n" + "=".repeat(60))
    println("   СПИСОК СФОРМИРОВАННЫХ КОНФИГУРАЦИЙ ДЛЯ СРАВНЕНИЯ")
    println("=".repeat(60))

    configList.forEachIndexed { index, item ->
        val type = item.type?.toString() ?: "ЛЮБОЙ"
        val size = item.size?.toString() ?: "ЛЮБОЙ"
        val side = item.side?.toString() ?: "ЛЮБОЙ"
        val weight = item.weight?.toString() ?: "ЛЮБОЙ"
        val color = item.color?.toString() ?: "ЛЮБОЙ"

        println(String.format("📍 [#%02d] Комплектация: Тип=%-6s | Размер=%-6s | Сторона=%-6s | Вес=%-6s | Цвет=%-6s",
            index + 1, type, size, side, weight, color))

        val config = item.configDto.config
        printPartsGroup("   ├── BaseParts  (Базовые)  : ", config.basePart)
        printPartsGroup("   ├── AddParts   (Добавочные): ", config.addParts)
        printPartsGroup("   └── DeleteParts(Удаляемые) : ", config.deleteParts)
        println("-" * 60)
    }
}

private fun printPartsGroup(prefix: String, items: List<Item>?) {
    if (items.isNullOrEmpty()) {
        println("$prefix[ПУСТО]")
    } else {
        val partsString = items.joinToString(separator = ", ") { "[ID:${it.id} x Кол-во:${it.q}]" }
        println("$prefix$partsString")
    }
}

private operator fun String.times(n: Int): String = this.repeat(n)
