package ru.lonelywh1te.introgym.features.auth.presentation.error

import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.BaseError
import ru.lonelywh1te.introgym.core.result.asStringRes
import ru.lonelywh1te.introgym.core.ui.ErrorStringResProvider
import ru.lonelywh1te.introgym.data.network.NetworkError
import ru.lonelywh1te.introgym.data.network.asStringRes
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthError
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthValidationError

object AuthErrorStringResProvider: ErrorStringResProvider {
    override fun get(error: BaseError): Int {
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
    is AuthError.InvalidEmailOrPassword -> R.string.auth_error_invalid_email_or_password
    is AuthError.EmailAlreadyRegistered -> R.string.auth_error_email_already_registered
    is AuthError.InvalidSession -> R.string.auth_error_session_timeout
    is AuthError.Unauthorized -> R.string.auth_error_unauthorized
    is AuthError.SessionStillExist -> R.string.auth_error_session_still_exist
    is AuthError.InvalidOtpCode -> R.string.auth_error_invalid_otp
    is AuthError.FailedToChangePassword -> R.string.auth_error_failed_to_change_password
}

fun AuthValidationError.asStringRes() = when (this) {
    is AuthValidationError.InvalidEmailFormat -> R.string.validation_error_invalid_email_format
    is AuthValidationError.InvalidPasswordFormat -> R.string.validation_error_invalid_password_format
    is AuthValidationError.PasswordMismatch -> R.string.validation_error_password_mismatch
    else ->  R.string.validation_error_invalid_password_format
}