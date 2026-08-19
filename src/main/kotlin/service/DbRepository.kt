package com.hornet.service

interface DbRepository {
    fun getAnyIdByAnyName(query: String?): Int?
    fun selectAllAnyTable(): List<Any>
}