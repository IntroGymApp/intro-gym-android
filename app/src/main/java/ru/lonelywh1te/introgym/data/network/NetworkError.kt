package ru.lonelywh1te.introgym.data.network

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.BaseError
import ru.lonelywh1te.introgym.core.result.Result
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException

sealed class NetworkError(
    override val throwable: Throwable?,
): BaseError {

    data class NoInternetConnection(
        override val throwable: Throwable? = null,
    ): NetworkError(throwable)

    data class RequestTimeout(
        override val throwable: Throwable? = null,
    ): NetworkError(throwable)

    data class ServerError(
        val code: Int,
        val message: String? = null,
        override val throwable: Throwable? = null,
    ): NetworkError(throwable)

    data class Unknown(
        val message: String? = null,
        override val throwable: Throwable? = null,
    ): NetworkError(throwable)
}

fun NetworkError.asStringRes() = when (this) {
    is NetworkError.NoInternetConnection -> R.string.network_error_no_internet
    is NetworkError.RequestTimeout -> R.string.network_error_request_timeout
    is NetworkError.ServerError -> R.string.network_error_server
    is NetworkError.Unknown -> R.string.label_network_error_unknown
}

fun <T> Flow<Result<T>>.asSafeNetworkFlow(): Flow<Result<T>> {
    val logTag = "safeNetworkFlow"

    return this
        .catch { e ->
            Log.e(logTag, e.toString())
            Log.e(logTag, e.stackTraceToString())

            when (e) {
                is UnknownHostException -> emit(Result.Failure(NetworkError.NoInternetConnection()))
                is SocketTimeoutException -> emit(Result.Failure(NetworkError.RequestTimeout()))
                is CancellationException -> throw e
                else -> emit(Result.Failure(AppError.Unknown(e)))
            }
        }
}