package ru.lonelywh1te.introgym.features.auth.domain.error

import ru.lonelywh1te.introgym.core.result.BaseError

sealed class AuthValidationError(override val message: String? = null, override val cause: Throwable? = null) : BaseError {

    data class InvalidEmailFormat(
        override val message: String? = null,
        override val cause: Throwable? = null,
    ) : AuthValidationError(message, cause)

    data class InvalidPasswordFormat(
        override val message: String? = null,
        override val cause: Throwable? = null,
    ) : AuthValidationError(message, cause)

    data class PasswordTooShort(
        override val message: String? = null,
        override val cause: Throwable? = null,
    ) : AuthValidationError(message, cause)

    data class PasswordMissingUppercase(
        override val message: String? = null,
        override val cause: Throwable? = null,
    ) : AuthValidationError(message, cause)

    data class PasswordMissingSpecialSymbol(
        override val message: String? = null,
        override val cause: Throwable? = null,
    ) : AuthValidationError(message, cause)

    data class PasswordMismatch(
        override val message: String? = null,
        override val cause: Throwable? = null,
    ) : AuthValidationError(message, cause)
}