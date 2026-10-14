package com.hornet.service

import com.hornet.dto.ConfigDto
import com.hornet.dto.ConfigInsertDto
import com.hornet.dto.ConfigParts
import com.hornet.dto.Item
import com.hornet.dto.PartsDto
import com.hornet.mappers.excelRowToParts
import com.hornet.mappers.listToUiConfigList
import com.hornet.mappers.mapToPartItem
import com.hornet.mappers.toCharCode
import com.hornet.mappers.toPairRanges
import com.hornet.model.app.Params
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.drop
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.io.readExcel

class ConfigService(
    private val repository: RepositoryInjection
) {
    fun getConfig(
        params: Params,
        resultList: MutableList<Item> = mutableListOf()
    ): List<PartsDto> {
        val pair = repository.configRepositoryImpl.getConfigurationIds(params = params)
        val addParts = pair.first
        val secondMap = pair.second

        if (addParts.isNullOrEmpty()) {
            throw IllegalArgumentException("Ошибка ввода: список addParts пуст или отсутствует.")
        }

        if (secondMap.isNullOrEmpty()) {
            resultList.addAll(addParts)
            val result = repository.partsRepositoryImpl.getPartsByIds(resultList.map { it.id })
            return result.listToUiConfigList(resultList)
        }

        addParts.forEach { item ->
            val result = secondMap[item.id]

            if (result != null) {
                if (result.q < item.q) {
                    resultList.add(Item(id = result.id, q = item.q - result.q))
                }
            } else {
                resultList.add(item)
            }
        }
        val result = repository.partsRepositoryImpl.getPartsByIds(resultList.map { it.id })

        return result.listToUiConfigList(resultList)
    }

    fun insertNewConfigByCalibrationCell(
        path: String,
        sheetName: String,
        row: Int = 2,
        column: Char = 'K',
        defaultColumnQuantity: Int = 9
    ): Result<List<ConfigInsertDto>> {
        val configList: MutableList<ConfigInsertDto> = mutableListOf()
        val frame: AnyFrame = DataFrame.readExcel(
            fileOrUrl = path,
            sheetName = sheetName,
            firstRowIsHeader = false,
        )

        val calColIndex = column.toCharCode()
        val calRowIndex = row - 1

        val strRanges = frame[calRowIndex][calColIndex].toString()
            .split("; ")
            .map { it.replace(";", "").trim() }

        val pairsRange = strRanges.toPairRanges()
        println(strRanges)
        println(pairsRange)

        pairsRange.forEach { pair ->
            val yScale = listOf(pair.first.first.toCharCode(), pair.second.first.toCharCode())
            val xScale = listOf(pair.first.second - 1, pair.second.second - 1)

            if (yScale[1] - yScale[0] < defaultColumnQuantity - 1) {
                return Result.failure(IndexOutOfBoundsException())
            }
            println(yScale)
            println(xScale)

            val rowCount = xScale[1] - xScale[0] + 1

            val selectedField = frame
                .drop(xScale[0])
                .take(rowCount)
                .select {
                    cols(yScale[0]..yScale[1])
                }

            println(selectedField)

            selectedField.rows().forEach { row ->
                val baseParts = row[6]?.toString()?.excelRowToParts()
                val addParts = row[7]?.toString()?.excelRowToParts()
                val removeParts = row[8]?.toString()?.excelRowToParts()

                configList.add(
                    ConfigInsertDto(
                        type = repository.furnitureTypeRepositoryImpl.getAnyIdByAnyName(
                            row[0]?.toString()
                        ),
                        size = repository.sizesRepositoryImpl.getAnyIdByAnyName(
                            row[1]?.toString()
                        ),
                        side = repository.partSideRepositoryImpl.getAnyIdByAnyName(
                            row[2]?.toString()
                        ),
                        weight = repository.partWeightRepositoryImpl.getAnyIdByAnyName(
                            row[3]?.toString()
                        ),
                        color = repository.partColorsRepositoryImpl.getAnyIdByAnyName(
                            row[4]?.toString()
                        ),
                        option = repository.partOptionsRepositoryImpl.getAnyIdByAnyName(
                            row[5]?.toString()
                        ),
                        configDto = ConfigDto(
                            config = ConfigParts(
                                basePart = mapToPartItem(
                                    repository.partsRepositoryImpl.getPartsIdsByPartsNames(baseParts?.first),
                                    baseParts?.first,
                                    baseParts?.second
                                ),
                                addParts = mapToPartItem(
                                    repository.partsRepositoryImpl.getPartsIdsByPartsNames(addParts?.first),
                                    addParts?.first,
                                    addParts?.second
                                ),
                                deleteParts = mapToPartItem(
                                    repository.partsRepositoryImpl.getPartsIdsByPartsNames(removeParts?.first),
                                    removeParts?.first,
                                    removeParts?.second
                                )
                            )
                        )
                    )
                )
            }
        }
        println(configList)
        repository.configRepositoryImpl.insertConfigInDb(configList)
        return Result.success(configList)
    }
}