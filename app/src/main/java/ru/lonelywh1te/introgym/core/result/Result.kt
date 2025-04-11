package ru.lonelywh1te.introgym.core.result

import ru.lonelywh1te.introgym.core.ui.UIState

sealed interface Result<out T> {
    data class Success<out T>(val data: T): Result<T>
    data object Loading: Result<Nothing>
    data class Failure(val error: Error): Result<Nothing>
}

inline fun <T> Result<T>.onSuccess(action: (data: T) -> Unit): Result<T> {
    if (this is Result.Success) action(this.data)
    return this
}

inline fun <T> Result<T>.onLoading(action: () -> Unit): Result<T> {
    if (this is Result.Loading) action()
    return this
}

inline fun <T> Result<T>.onFailure(action: (Error) -> Unit): Result<T> {
    if (this is Result.Failure) action(this.error)
    return this
}

fun Result<*>.toUIState(): UIState<*>  {
    return when (this) {
        is Result.Loading -> UIState.Loading
        is Result.Failure -> UIState.Failure(this.error)
        is Result.Success -> UIState.Success(this.data)
    }
}