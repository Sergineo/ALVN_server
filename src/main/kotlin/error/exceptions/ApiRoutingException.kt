package com.hornet.error.exceptions

open class ApiRoutingExceptions(description: String = "") : Exception(description)

class IncorrectRequestBody :
    ApiRoutingExceptions("Параметры запроса не соответствуют данному URL")

class NonExistId :
    ApiRoutingExceptions("Некорректный формат числа id")