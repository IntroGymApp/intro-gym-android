package ru.lonelywh1te.introgym.core.ui

import ru.lonelywh1te.introgym.core.result.Error

sealed interface UIState<out T> {
    data class Success<out T>(val data: T): UIState<T>
    data object isLoading: UIState<Nothing>
    data class Failure(val error: Error): UIState<Nothing>
    data object Init: UIState<Nothing>
}