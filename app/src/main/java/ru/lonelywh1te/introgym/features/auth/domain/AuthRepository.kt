package ru.lonelywh1te.introgym.features.auth.domain

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result

interface AuthRepository {
    fun sendOtp(email: String, otpType: String): Flow<Result<Unit>>
    fun confirmOtp(otp: String, otpType: String): Flow<Result<Unit>>

    fun signUp(email: String, password: String): Flow<Result<Unit>>
    fun signIn(email: String, password: String): Flow<Result<Unit>>

    fun changePassword(email: String, password: String): Flow<Result<Unit>>
    fun refreshToken(): Flow<Result<Unit>>

    fun isSignedIn(): Boolean
}