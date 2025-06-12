package ru.lonelywh1te.introgym.core.result

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.SharedFlow

interface ErrorDispatcher {
    val errorMessages: SharedFlow<String>

    fun dispatch(error: BaseError, @StringRes messageRes: Int? = null)
}