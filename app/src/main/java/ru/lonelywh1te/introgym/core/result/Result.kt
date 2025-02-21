package ru.lonelywh1te.introgym.core.result

sealed interface Result<out T> {
    data class Success<out T>(val data: T): Result<T>
    data object InProgress: Result<Nothing>
    data class Failure(val error: Error): Result<Nothing>
}