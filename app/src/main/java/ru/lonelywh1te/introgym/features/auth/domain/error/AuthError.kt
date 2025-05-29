package ru.lonelywh1te.introgym.features.auth.domain.error

import ru.lonelywh1te.introgym.core.result.BaseError

sealed class AuthError(
    override val message: String?,
    override val cause: Throwable?,
): BaseError {

    data class InvalidOtpCode(
        override val message: String? = null,
        override val cause: Throwable? = null
    ) : AuthError(message, cause)

    data class SessionStillExist(
        override val message: String? = null,
        override val cause: Throwable? = null
    ) : AuthError(message, cause)

    data class InvalidSession(
        override val message: String? = null,
        override val cause: Throwable? = null
    ) : AuthError(message, cause)

    data class EmailAlreadyRegistered(
        override val message: String? = null,
        override val cause: Throwable? = null
    ) : AuthError(message, cause)

    data class Unauthorized(
        override val message: String? = null,
        override val cause: Throwable? = null
    ) : AuthError(message, cause)

    data class InvalidEmailOrPassword(
        override val message: String? = null,
        override val cause: Throwable? = null
    ) : AuthError(message, cause)

    data class FailedToChangePassword(
        override val message: String? = null,
        override val cause: Throwable? = null
    ) : AuthError(message, cause)

}