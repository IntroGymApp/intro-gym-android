package ru.lonelywh1te.introgym.data.network

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.BaseError
import ru.lonelywh1te.introgym.core.result.Result
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException

sealed class NetworkError(
    override val message: String?,
    override val cause: Throwable?,
): BaseError {

    data class NoInternetConnection(
        override val message: String? = null,
        override val cause: Throwable? = null,
    ): NetworkError(message, cause)

    data class RequestTimeout(
        override val message: String? = null,
        override val cause: Throwable? = null
    ): NetworkError(message, cause)

    data class ServerError(
        val code: Int,
        override val message: String? = null,
        override val cause: Throwable? = null,
    ): NetworkError(message, cause)

    data class Unknown(
        override val message: String? = null,
        override val cause: Throwable? = null,
    ): NetworkError(message, cause)
}

fun NetworkError.asStringRes() = when (this) {
    is NetworkError.NoInternetConnection -> R.string.network_error_no_internet
    is NetworkError.RequestTimeout -> R.string.network_error_request_timeout
    is NetworkError.ServerError -> R.string.network_error_server
    is NetworkError.Unknown -> TODO("No network error stringRes")
}

fun <T> Flow<Result<T>>.asSafeNetworkFlow(): Flow<Result<T>> = flow {
    val logTag = "NETWORK_FLOW"

    try {
        collect { emit(it) }
    } catch (e: UnknownHostException) {
        emit(Result.Failure(NetworkError.NoInternetConnection()))
        Log.e(logTag, "UNKNOWN HOST EXCEPTION: $e")
    } catch (e: SocketTimeoutException) {
        emit(Result.Failure(NetworkError.RequestTimeout()))
        Log.e(logTag, "REQUEST_TIMEOUT: $e")
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.e(logTag, "UNKNOWN_EXCEPTION: $e")
        emit(Result.Failure(AppError.Unknown(e.message, e)))
    }
}