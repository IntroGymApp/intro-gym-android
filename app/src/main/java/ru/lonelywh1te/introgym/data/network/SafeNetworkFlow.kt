package ru.lonelywh1te.introgym.data.network

import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.lonelywh1te.introgym.core.result.Result
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun <T> Flow<Result<T>>.asSafeNetworkFlow(): Flow<Result<T>> = flow {
    val logTag = "NETWORK_FLOW"

    try {
        collect { emit(it) }
    } catch (e: UnknownHostException) {
        emit(Result.Failure(NetworkError.NO_INTERNET))
        Log.e(logTag, "UNKNOWN HOST EXCEPTION: $e")
    } catch (e: SocketTimeoutException) {
        emit(Result.Failure(NetworkError.REQUEST_TIMEOUT))
        Log.e(logTag, "REQUEST_TIMEOUT: $e")
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.e(logTag, "UNKNOWN_EXCEPTION: $e")
        emit(Result.Failure(NetworkError.UNKNOWN))
    }
}