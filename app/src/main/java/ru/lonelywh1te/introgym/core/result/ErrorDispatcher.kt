package ru.lonelywh1te.introgym.core.result

import kotlinx.coroutines.flow.SharedFlow

interface ErrorDispatcher {
    val errorMessages: SharedFlow<String>

    fun dispatch(error: BaseError, message: String? = null)
}