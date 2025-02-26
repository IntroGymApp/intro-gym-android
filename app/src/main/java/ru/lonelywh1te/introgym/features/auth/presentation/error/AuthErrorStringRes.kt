package ru.lonelywh1te.introgym.features.auth.presentation.error

import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthError
import ru.lonelywh1te.introgym.features.auth.domain.error.ValidationError

fun AuthError.asStringRes() = when (this) {
    AuthError.INVALID_EMAIL_OR_PASSWORD -> R.string.auth_error_invalid_email_or_password
    AuthError.EMAIL_ALREADY_REGISTERED -> R.string.auth_error_email_already_registered
    AuthError.INVALID_SESSION -> R.string.auth_error_session_timeout
    AuthError.UNAUTHORIZED -> R.string.auth_error_unauthorized
    AuthError.SESSION_STILL_EXIST -> R.string.auth_error_session_still_exist
    AuthError.INVALID_OTP_CODE -> R.string.auth_error_invalid_otp
}

fun ValidationError.asStringRes() = when (this) {
    ValidationError.INVALID_EMAIL_FORMAT -> R.string.validation_error_invalid_email_format
    ValidationError.INVALID_PASSWORD_FORMAT -> R.string.validation_error_invalid_password_format
    ValidationError.PASSWORD_MISMATCH -> R.string.validation_error_password_mismatch
}