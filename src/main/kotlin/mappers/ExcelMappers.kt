package com.hornet.mappers

import com.hornet.dto.ConfigDto
import com.hornet.dto.ConfigParts
import com.hornet.dto.Item
import kotlin.collections.chunked

//fun AnyFrame.toExcelDto() = ConfigInsertDto(
//    type = this,
//    size = TODO(),
//    side = TODO(),
//    weight = TODO(),
//    color = TODO(),
//    configDto = TODO()
//)

fun List<String>.toPairRanges(): List<Pair<Pair<Char, Int>, Pair<Char, Int>>> {
    val parsedPoints = this.map { coordinateStr ->
        val parts = coordinateStr.split(",")
        val colChar = parts.getOrNull(0)?.trim()?.firstOrNull() ?: 'A'
        val rowInt = parts.getOrNull(1)?.trim()?.toIntOrNull() ?: 1
        Pair(colChar, rowInt)
    }

    return parsedPoints.chunked(2).mapNotNull { chunk ->
        if (chunk.size == 2) {
            Pair(chunk[0], chunk[1])
        } else {
            null
        }
    }
}

fun Char.toCharCode() = this.uppercaseChar() - 'A'


fun String?.excelRowToParts(): Pair<List<String>, List<Int>>? {
    if (this.isNullOrBlank()) return null

    val stringLst: MutableList<String> = mutableListOf()
    val intLst: MutableList<Int> = mutableListOf()

    this.split(";")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .forEach { token ->
            val parts = token.split(",")
            val name = parts.getOrNull(0)?.trim()
            val quantity = parts.getOrNull(1)?.trim()?.toIntOrNull()

            if (!name.isNullOrEmpty() && quantity != null) {
                stringLst.add(name)
                intLst.add(quantity)
            }
        }

    return if (stringLst.isEmpty()) null else Pair(stringLst, intLst)
}

fun mapToPartItem(
    articleToIdMap: Map<String, Int>?,
    articlesOrder: List<String>?,
    quantities: List<Int>?
): List<Item>? {
    if (articleToIdMap == null || articlesOrder == null || quantities == null) return null

    return articlesOrder.mapIndexedNotNull { index, article ->
        val id = articleToIdMap[article]
        val q = quantities.getOrNull(index)

        if (id != null && q != null) {
            Item(id = id, q = q)
        } else null
    }
}