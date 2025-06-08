package ru.lonelywh1te.introgym.features.auth.domain

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.auth.domain.model.SignUpCredentials
import ru.lonelywh1te.introgym.features.auth.domain.model.TokenPair
import ru.lonelywh1te.introgym.features.auth.domain.model.UserCredentials

interface AuthRepository {
    fun signUp(signUpData: SignUpCredentials): Flow<Result<Unit>>
    fun signIn(signInData: UserCredentials): Flow<Result<Unit>>

    fun changePassword(data: UserCredentials): Flow<Result<Unit>>
    suspend fun refreshToken(): Result<TokenPair>

    fun isSignedIn(): Boolean
}