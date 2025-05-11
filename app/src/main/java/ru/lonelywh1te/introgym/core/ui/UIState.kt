package ru.lonelywh1te.introgym.core.ui

import ru.lonelywh1te.introgym.core.result.BaseError

sealed interface UIState<out T> {
    data class Success<out T>(val data: T): UIState<T>
    data object Loading: UIState<Nothing>
    data class Failure(val error: BaseError): UIState<Nothing>
}