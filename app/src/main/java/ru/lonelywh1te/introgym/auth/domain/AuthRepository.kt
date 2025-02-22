package ru.lonelywh1te.introgym.auth.domain

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.auth.domain.model.ConfirmOtpResult
import ru.lonelywh1te.introgym.auth.domain.model.SendOtpResult
import ru.lonelywh1te.introgym.auth.domain.model.Token
import ru.lonelywh1te.introgym.core.result.Result

interface AuthRepository {
    suspend fun sendOtp(email: String): Flow<Result<SendOtpResult>>
    suspend fun confirmOtp(otp: String): Flow<Result<ConfirmOtpResult>>
    suspend fun signUp(email: String, password: String): Flow<Result<Token>>
    suspend fun signIn(email: String, password: String): Flow<Result<Token>>
    suspend fun refreshToken(): Flow<Result<Token>>
}