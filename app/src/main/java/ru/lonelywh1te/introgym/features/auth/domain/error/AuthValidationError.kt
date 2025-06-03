package ru.lonelywh1te.introgym.features.auth.domain.error

import ru.lonelywh1te.introgym.core.result.BaseError

sealed class AuthValidationError(
    override val throwable: Throwable? = null
): BaseError {

    data class InvalidEmailFormat(
        override val throwable: Throwable? = null,
    ) : AuthValidationError(throwable)

    data class InvalidPasswordFormat(
        override val throwable: Throwable? = null,
    ) : AuthValidationError(throwable)

    data class PasswordTooShort(
        override val throwable: Throwable? = null,
    ) : AuthValidationError(throwable)

    data class PasswordMissingUppercase(
        override val throwable: Throwable? = null,
    ) : AuthValidationError(throwable)

    data class PasswordMissingSpecialSymbol(
        override val throwable: Throwable? = null,
    ) : AuthValidationError(throwable)

    data class PasswordMismatch(
        override val throwable: Throwable? = null,
    ) : AuthValidationError(throwable)

    data class InvalidUsernameFormat(
        override val throwable: Throwable? = null,
    ) : AuthValidationError(throwable)
}