package ru.lonelywh1te.introgym.core.result

import kotlinx.coroutines.flow.SharedFlow

interface ErrorDispatcher {
    val errors: SharedFlow<BaseError>

    fun dispatch(error: BaseError)
}