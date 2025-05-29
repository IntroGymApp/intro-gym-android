package ru.lonelywh1te.introgym.data.db

import android.database.sqlite.SQLiteException
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.BaseError
import ru.lonelywh1te.introgym.core.result.Result
import kotlin.coroutines.cancellation.CancellationException

// TODO: Уточнить список ошибок базы данных

sealed class DatabaseError(
    override val message: String?,
    override val cause: Throwable?,
): BaseError {

    data class SQLiteError(
        override val message: String? = null,
        override val cause: Throwable?,
    ): DatabaseError(message, cause)

}

fun DatabaseError.toStringRes() = when(this) {
    is DatabaseError.SQLiteError -> R.string.label_sqlite_error
}

fun <T> Flow<Result<T>>.asSafeSQLiteFlow(): Flow<Result<T>> = flow {
    val logTag = "SafeSQLiteFlow"

    try {
        collect { emit(it) }
    } catch (e: Exception) {
        Log.e(logTag, "An error occurred while executing the database action", e)

        emit(
            when (e) {
                is SQLiteException -> Result.Failure(DatabaseError.SQLiteError(e.message, e))
                is CancellationException -> throw e
                else -> Result.Failure(AppError.Unknown(e.message, e))
            }
        )
    }
}

inline fun <T> sqliteTryCatching(action: () -> T): Result<T> {
    val logTag = "sqliteTryCatching"

    return try {
        Result.Success(action())
    } catch (e: Exception) {
        Log.e(logTag, "An error occurred while executing the database action", e)

        when (e) {
            is SQLiteException -> Result.Failure(DatabaseError.SQLiteError(e.message, e))
            else -> Result.Failure(AppError.Unknown(e.message, e))
        }
    }
}