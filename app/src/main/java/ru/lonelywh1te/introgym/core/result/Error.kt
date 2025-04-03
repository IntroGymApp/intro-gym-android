package ru.lonelywh1te.introgym.core.result

import ru.lonelywh1te.introgym.R
import java.io.Serializable

interface Error: Serializable

enum class AppError: Error {
    UNKNOWN,
}

fun AppError.asStringRes() = when (this) {
    AppError.UNKNOWN -> R.string.app_error_unknown
}