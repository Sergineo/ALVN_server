package com.hornet.repos.db

import com.hornet.service.RepositoryInjection

class RepositoryInjectionImpl(
    override val partsRepositoryImpl: PartsRepositoryImpl,
    override val configRepositoryImpl: ConfigRepositoryImpl,
    override val furnitureTypeRepositoryImpl: FurnitureTypeRepositoryImpl,
    override val sizesRepositoryImpl: SizesRepositoryImpl,
    override val partSideRepositoryImpl: PartSideRepositoryImpl,
    override val partWeightRepositoryImpl: PartWeightRepositoryImpl,
    override val partColorsRepositoryImpl: PartColorsRepositoryImpl,
    override val partOptionsRepositoryImpl: PartOptionsRepositoryImpl
): RepositoryInjection