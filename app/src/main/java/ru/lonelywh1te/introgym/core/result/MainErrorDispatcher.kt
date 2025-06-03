package ru.lonelywh1te.introgym.core.result

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest

class MainErrorDispatcher: ErrorDispatcher {
    private val _errorMessages: MutableSharedFlow<String> = MutableSharedFlow(extraBufferCapacity = 1)
    override val errorMessages: SharedFlow<String> = _errorMessages.asSharedFlow()

    override fun dispatch(error: BaseError, message: String?) {
        _errorMessages.tryEmit(message ?: error.throwable?.message ?: "$error")
        Log.e("ErrorDispatcher", "$error\n${error.throwable?.stackTraceToString()}")
    }
}