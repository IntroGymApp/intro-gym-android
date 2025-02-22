package ru.lonelywh1te.introgym.core.network

import ru.lonelywh1te.introgym.core.result.Error

enum class NetworkError: Error {
    NO_INTERNET,
    REQUEST_TIMEOUT,
    SERVER_ERROR,
    UNKNOWN,
}