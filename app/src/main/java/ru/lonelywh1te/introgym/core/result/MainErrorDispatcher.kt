package ru.lonelywh1te.introgym.core.result

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class MainErrorDispatcher(
    private val context: Context,
): ErrorDispatcher {
    private val _errorMessages: MutableSharedFlow<String> = MutableSharedFlow(extraBufferCapacity = 1)
    override val errorMessages: SharedFlow<String> = _errorMessages.asSharedFlow()

    override fun dispatch(error: BaseError, messageRes: Int?) {
        _errorMessages.tryEmit(messageRes?.let { context.getString(it) } ?: error.throwable?.message ?: "$error")
        Log.e("ErrorDispatcher", "$error\n${error.throwable?.stackTraceToString()}")
    }
}