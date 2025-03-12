package ru.lonelywh1te.introgym.features.auth.domain.error

import ru.lonelywh1te.introgym.core.result.Error

enum class AuthError: Error {
    INVALID_OTP_CODE,
    SESSION_STILL_EXIST,
    INVALID_SESSION,
    EMAIL_ALREADY_REGISTERED,
    UNAUTHORIZED,
    INVALID_EMAIL_OR_PASSWORD,
    FAILED_TO_CHANGE_PASSWORD,
}