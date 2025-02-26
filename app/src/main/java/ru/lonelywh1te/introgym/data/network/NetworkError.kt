package ru.lonelywh1te.introgym.data.network

import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.result.Error

enum class NetworkError: Error {
    NO_INTERNET,
    REQUEST_TIMEOUT,
    SERVER_ERROR,
    UNKNOWN,
}

fun NetworkError.asStringRes() = when (this) {
    NetworkError.NO_INTERNET -> R.string.network_error_no_internet
    NetworkError.UNKNOWN -> R.string.network_error_unknown
    NetworkError.REQUEST_TIMEOUT -> R.string.network_error_request_timeout
    NetworkError.SERVER_ERROR -> R.string.network_error_server
}