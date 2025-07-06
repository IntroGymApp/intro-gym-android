package ru.lonelywh1te.introgym.data.db

import android.database.sqlite.SQLiteException
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.BaseError
import ru.lonelywh1te.introgym.core.result.Result
import kotlin.coroutines.cancellation.CancellationException

// TODO: Уточнить список ошибок базы данных

sealed class DatabaseError(
    override val throwable: Throwable?,
): BaseError {

    data class SQLiteError(
        override val throwable: Throwable?,
    ): DatabaseError(throwable)

}

fun DatabaseError.toStringRes() = when(this) {
    is DatabaseError.SQLiteError -> R.string.label_sqlite_error
}

fun <T> Flow<Result<T>>.asSafeSQLiteFlow(): Flow<Result<T>> {
    val logTag = "SafeSQLiteFlow"

    return this
        .catch {e ->
            Log.e(logTag, "An error occurred while executing the database action", e)
            Log.e(logTag, e.stackTraceToString())

            when (e) {
                is SQLiteException -> emit(Result.Failure(DatabaseError.SQLiteError(e)))
                is CancellationException -> throw e
                else -> emit(Result.Failure(AppError.Unknown(e)))
            }
        }
}

inline fun <T> sqliteTryCatching(action: () -> T): Result<T> {
    val logTag = "sqliteTryCatching"

    return try {
        Result.Success(action())
    } catch (e: Exception) {
        Log.e(logTag, "An error occurred while executing the database action", e)

        when (e) {
            is SQLiteException -> Result.Failure(DatabaseError.SQLiteError(e))
            else -> Result.Failure(AppError.Unknown(e))
        }
    }
}