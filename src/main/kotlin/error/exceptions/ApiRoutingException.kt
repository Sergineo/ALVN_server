package com.hornet.error.exceptions

sealed class ApiRoutingException(val description: String = "") : Exception(description)

class IncorrectRequestBodyException :
    ApiRoutingException("Параметры запроса не соответствуют данному URL")

class NonExistIdException :
    ApiRoutingException("Некорректный формат числа id")