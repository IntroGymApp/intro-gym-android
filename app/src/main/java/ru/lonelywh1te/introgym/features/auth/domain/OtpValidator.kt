package ru.lonelywh1te.introgym.features.auth.domain

import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthError

object OtpValidator {
    fun validate(otp: String): Result<Unit> {
        return if (otp.length == 5) Result.Success(Unit) else Result.Failure(AuthError.InvalidOtpCode())
    }
}