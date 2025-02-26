package ru.lonelywh1te.introgym.features.auth.domain.error

import ru.lonelywh1te.introgym.core.result.Error

enum class ValidationError: Error {
    INVALID_EMAIL_FORMAT,
    INVALID_PASSWORD_FORMAT,
    PASSWORD_MISMATCH,
}