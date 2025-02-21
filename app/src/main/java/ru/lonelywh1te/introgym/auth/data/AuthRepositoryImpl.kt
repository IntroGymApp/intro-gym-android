package ru.lonelywh1te.introgym.auth.data

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.auth.domain.model.ConfirmOtpResult
import ru.lonelywh1te.introgym.auth.domain.model.SendOtpResult
import ru.lonelywh1te.introgym.auth.domain.model.Token
import ru.lonelywh1te.introgym.core.result.Result

class AuthRepositoryImpl(val authService: AuthService): AuthRepository {
    override suspend fun sendOtp(email: String): Flow<Result<SendOtpResult>> {
        TODO("Not yet implemented")
    }

    override suspend fun confirmOtp(otp: String): Flow<Result<ConfirmOtpResult>> {
        TODO("Not yet implemented")
    }

    override suspend fun signUp(email: String, password: String): Flow<Result<Token>> {
        TODO("Not yet implemented")
    }

    override suspend fun signIn(email: String, password: String): Flow<Result<Token>> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshToken(refreshToken: String): Flow<Result<Token>> {
        TODO("Not yet implemented")
    }
}