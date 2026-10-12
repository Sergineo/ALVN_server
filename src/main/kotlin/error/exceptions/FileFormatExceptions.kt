package com.hornet.error.exceptions

sealed class FileFormatException(message: String = "") : Exception(message)

class UnextensibleImageFormat(extension: String):
    FileFormatException("Неподдерживаемое расширение изображения: .$extension")