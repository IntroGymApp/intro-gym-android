package ru.lonelywh1te.introgym.features.auth.domain.error

import ru.lonelywh1te.introgym.core.result.BaseError

sealed class AuthError(
    override val throwable: Throwable?,
): BaseError {

    data class InvalidOtpCode(
        override val throwable: Throwable? = null
    ) : AuthError(throwable)

    data class SessionStillExist(
        override val throwable: Throwable? = null
    ) : AuthError(throwable)

    data class InvalidSession(
        override val throwable: Throwable? = null
    ) : AuthError(throwable)

    data class EmailAlreadyRegistered(
        override val throwable: Throwable? = null
    ) : AuthError(throwable)

    data class Unauthorized(
        override val throwable: Throwable? = null
    ) : AuthError(throwable)

    data class InvalidEmailOrPassword(
        override val throwable: Throwable? = null
    ) : AuthError(throwable)

    data class FailedToChangePassword(
        override val throwable: Throwable? = null
    ) : AuthError(throwable)

}