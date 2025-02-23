package ru.lonelywh1te.introgym.auth.domain

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result

interface AuthRepository {
    suspend fun sendOtp(email: String, otpType: String): Flow<Result<Unit>>
    suspend fun confirmOtp(otp: String, otpType: String): Flow<Result<Unit>>
    suspend fun signUp(email: String, password: String): Flow<Result<Unit>>
    suspend fun signIn(email: String, password: String): Flow<Result<Unit>>
    suspend fun refreshToken(): Flow<Result<Unit>>
}