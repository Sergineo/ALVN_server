package com.hornet.error.exceptions

sealed class ImageExceptions(description: String = "") : Exception(description)

class IncorrectImagePath :
    ImageExceptions("Неправильный путь к папке ресурсов переменной окружения")