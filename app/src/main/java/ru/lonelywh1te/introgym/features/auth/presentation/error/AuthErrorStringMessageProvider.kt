package ru.lonelywh1te.introgym.features.auth.presentation.error

import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.result.asStringRes
import ru.lonelywh1te.introgym.core.ui.ErrorStringResProvider
import ru.lonelywh1te.introgym.data.network.NetworkError
import ru.lonelywh1te.introgym.data.network.asStringRes
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthError
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthValidationError

object AuthErrorStringResProvider: ErrorStringResProvider {
    override fun get(error: Error): Int {
        return when (error) {
            is AuthError -> error.asStringRes()
            is AuthValidationError -> error.asStringRes()
            is NetworkError -> error.asStringRes()
            is AppError -> error.asStringRes()
            else -> throw IllegalArgumentException("Unknown Error type: $this")
        }
    }
}

fun AuthError.asStringRes() = when (this) {
    AuthError.INVALID_EMAIL_OR_PASSWORD -> R.string.auth_error_invalid_email_or_password
    AuthError.EMAIL_ALREADY_REGISTERED -> R.string.auth_error_email_already_registered
    AuthError.INVALID_SESSION -> R.string.auth_error_session_timeout
    AuthError.UNAUTHORIZED -> R.string.auth_error_unauthorized
    AuthError.SESSION_STILL_EXIST -> R.string.auth_error_session_still_exist
    AuthError.INVALID_OTP_CODE -> R.string.auth_error_invalid_otp
    AuthError.FAILED_TO_CHANGE_PASSWORD -> R.string.auth_error_failed_to_change_password
}

fun AuthValidationError.asStringRes() = when (this) {
    AuthValidationError.INVALID_EMAIL_FORMAT -> R.string.validation_error_invalid_email_format
    AuthValidationError.INVALID_PASSWORD_FORMAT -> R.string.validation_error_invalid_password_format
    AuthValidationError.PASSWORD_MISMATCH -> R.string.validation_error_password_mismatch
    else ->  R.string.validation_error_invalid_password_format
}