package ru.lonelywh1te.introgym.features.auth.domain

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result

interface OtpRepository {
    fun sendOtp(email: String, otpType: String): Flow<Result<Unit>>
    fun confirmOtp(otp: String, otpType: String): Flow<Result<Unit>>
}