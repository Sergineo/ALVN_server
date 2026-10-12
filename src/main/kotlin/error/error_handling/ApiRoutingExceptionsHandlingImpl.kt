package com.hornet.error.error_handling

import com.hornet.error.exceptions.IncorrectRequestBodyException
import com.hornet.error.exceptions.NonExistIdException

class ApiRoutingExceptionsHandlingImpl(
    private val incorrectRequestBodyException: IncorrectRequestBodyException =
        IncorrectRequestBodyException(),
    private val nonExistIdException: NonExistIdException =
        NonExistIdException()
) {
    fun checkFormatIdForWeightRequest(
        id: Int?,
        callback: (id: Int) -> List<Any>,
    ): List<Any> {
        if (id == null) throw incorrectRequestBodyException
        if (id < 0) throw nonExistIdException
        val result: List<Any> = callback(id)
        if (result.isEmpty()) {
            throw nonExistIdException
        }

        return result
    }

    fun checkFormatNullableIdForConfigRequest(
        id: Int?,
        callback: (id: Int?) -> Boolean,
    ): Int? {
        if (id != null && id < 0) throw nonExistIdException
        if (!callback(id)) {
            throw nonExistIdException
        }

        return id
    }
    fun checkFormatIdForConfigRequest(
        id: Int,
        callback: (id: Int) -> Boolean,
    ): Int {
        if (id < 0) throw nonExistIdException
        if (!callback(id)) {
            throw nonExistIdException
        }

        return id
    }
}