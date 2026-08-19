package com.hornet.service

import com.hornet.repos.db.PartColorsRepositoryImpl
import com.hornet.repos.db.ConfigRepositoryImpl
import com.hornet.repos.db.FurnitureTypeRepositoryImpl
import com.hornet.repos.db.PartOptionsRepositoryImpl
import com.hornet.repos.db.PartSideRepositoryImpl
import com.hornet.repos.db.PartWeightRepositoryImpl
import com.hornet.repos.db.PartsRepositoryImpl
import com.hornet.repos.db.SizesRepositoryImpl

interface RepositoryInjection {
    val configRepositoryImpl: ConfigRepositoryImpl
    val partsRepositoryImpl: PartsRepositoryImpl
    val furnitureTypeRepositoryImpl: FurnitureTypeRepositoryImpl
    val sizesRepositoryImpl: SizesRepositoryImpl
    val partSideRepositoryImpl: PartSideRepositoryImpl
    val partWeightRepositoryImpl: PartWeightRepositoryImpl
    val partColorsRepositoryImpl: PartColorsRepositoryImpl
    val partOptionsRepositoryImpl: PartOptionsRepositoryImpl
}