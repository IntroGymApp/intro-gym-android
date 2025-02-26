package ru.lonelywh1te.introgym.core.result

import ru.lonelywh1te.introgym.core.ui.UIState

sealed interface Result<out T> {
    data class Success<out T>(val data: T): Result<T>
    data object InProgress: Result<Nothing>
    data class Failure(val error: Error): Result<Nothing>
}

fun Result<*>.toUIState(): UIState<*>  {
    return when (this) {
        is Result.InProgress -> UIState.isLoading
        is Result.Failure -> UIState.Failure(this.error)
        is Result.Success -> UIState.Success(this.data)
    }
}