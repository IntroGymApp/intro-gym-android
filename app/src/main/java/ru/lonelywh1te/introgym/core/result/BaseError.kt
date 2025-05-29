package ru.lonelywh1te.introgym.core.result

import ru.lonelywh1te.introgym.R
import java.io.Serializable

interface BaseError: Serializable {
    val message: String?
    val cause: Throwable?
}

sealed class AppError: BaseError {
    data class Unknown(
        override val message: String? = null,
        override val cause: Throwable?,
    ): AppError()
}

fun AppError.asStringRes() = when (this) {
    is AppError.Unknown -> R.string.app_error_unknown
}