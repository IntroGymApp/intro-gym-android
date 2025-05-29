package ru.lonelywh1te.introgym.core.result

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class MainErrorDispatcher: ErrorDispatcher {
    private val _errors: MutableSharedFlow<BaseError> = MutableSharedFlow(extraBufferCapacity = 1)
    override val errors get() = _errors.asSharedFlow()

    override fun dispatch(error: BaseError) {
        _errors.tryEmit(error)
        Log.e("ErrorDispatcher", "ERROR DISPATCHED: $error | ${error.message}")
        error.cause?.let { Log.e("ErrorDispatcher", it.stackTraceToString()) }
    }

}