package ru.lonelywh1te.introgym.features.auth.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.data.network.NetworkError
import ru.lonelywh1te.introgym.data.network.asSafeNetworkFlow
import ru.lonelywh1te.introgym.data.prefs.UserPreferences
import ru.lonelywh1te.introgym.features.auth.data.dto.ChangePasswordRequestDto
import ru.lonelywh1te.introgym.features.auth.data.dto.ConfirmOtpRequestDto
import ru.lonelywh1te.introgym.features.auth.data.dto.RefreshTokensRequestDto
import ru.lonelywh1te.introgym.features.auth.data.dto.SendOtpRequestDto
import ru.lonelywh1te.introgym.features.auth.data.dto.SignInRequestDto
import ru.lonelywh1te.introgym.features.auth.data.dto.SignUpRequestDto
import ru.lonelywh1te.introgym.features.auth.data.storage.AuthStorage
import ru.lonelywh1te.introgym.features.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthError

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val authStorage: AuthStorage,
    private val userPreferences: UserPreferences,
): AuthRepository {
    override suspend fun sendOtp(email: String, otpType: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)

        val response = authService.sendOtp(SendOtpRequestDto(email), otpType)
        val body = response.body()

        val result = when {
            response.isSuccessful && body != null -> Result.Success(Unit)
            response.code() == 409 -> Result.Failure(AuthError.SessionStillExist())
            response.code() in 500..599 -> Result.Failure(NetworkError.ServerError(response.code(), response.errorBody()?.string()))
            else -> Result.Failure(NetworkError.Unknown(message = response.errorBody()?.string()))
        }.also {
            if (it is Result.Success && body != null) authStorage.saveSessionId(body.sessionId)
        }

        emit(result)
    }.asSafeNetworkFlow()

    override suspend fun confirmOtp(otp: String, otpType: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)

        val sessionId = authStorage.getSessionId() ?: ""

        val response = authService.confirmOtp(ConfirmOtpRequestDto(sessionId, otp), otpType)
        val body = response.body()

        val result = when {
            response.isSuccessful && body != null && body.isSuccess -> Result.Success(Unit)
            response.isSuccessful && body != null && !body.isSuccess -> Result.Failure(AuthError.InvalidOtpCode())
            response.code() == 400 -> Result.Failure(AuthError.InvalidSession())
            response.code() in 500..599 -> Result.Failure(NetworkError.ServerError(response.code(), response.errorBody()?.string()))
            else -> Result.Failure(NetworkError.Unknown(message = response.errorBody()?.string()))
        }

        if (!response.isSuccessful) Log.w(LOG_TAG, "FAIL: $response")

        emit(result)
    }.asSafeNetworkFlow()

    override suspend fun signUp(email: String, password: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)

        val username = userPreferences.username ?: ""
        val sessionId = authStorage.getSessionId() ?: ""

        val response = authService.signUp(SignUpRequestDto(sessionId, username, email, password))
        val body = response.body()

        val result = when {
            response.isSuccessful && body != null -> Result.Success(Unit)
            response.code() == 409 -> Result.Failure(AuthError.EmailAlreadyRegistered())
            response.code() == 400 -> Result.Failure(AuthError.InvalidSession())
            response.code() in 500..599 -> Result.Failure(NetworkError.ServerError(response.code(), response.errorBody()?.string()))
            else -> Result.Failure(NetworkError.Unknown(message = response.errorBody()?.string()))
        }.also {
            if (it is Result.Success && body != null) authStorage.saveTokens(body.accessToken, body.refreshToken)
            authStorage.clearSessionId()
        }

        if (!response.isSuccessful) Log.w(LOG_TAG, "FAIL: $response")

        emit(result)
    }.asSafeNetworkFlow()

    override suspend fun signIn(email: String, password: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)

        val response = authService.signIn(SignInRequestDto(email, password))
        val body = response.body()

        val result = when {
            response.isSuccessful && body != null -> Result.Success(Unit)
            response.code() == 400 -> Result.Failure(AuthError.InvalidEmailOrPassword())
            response.code() in 500..599 -> Result.Failure(NetworkError.ServerError(response.code(), response.errorBody()?.string()))
            else -> Result.Failure(NetworkError.Unknown(message = response.errorBody()?.string()))
        }.also {
            if (it is Result.Success && body != null) authStorage.saveTokens(body.accessToken, body.refreshToken)
        }

        if (!response.isSuccessful) Log.w(LOG_TAG, "FAIL: $response")

        emit(result)
    }.asSafeNetworkFlow()

    override suspend fun changePassword(email: String, password: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)

        val sessionId = authStorage.getSessionId() ?: ""

        val response = authService.changePassword(sessionId, ChangePasswordRequestDto(email, password))
        val body = response.body()

        val result = when {
            response.isSuccessful && body != null && body.isSuccess -> Result.Success(Unit)
            response.isSuccessful && body != null && !body.isSuccess -> Result.Failure(AuthError.FailedToChangePassword())
            response.code() in 500..599 -> Result.Failure(NetworkError.ServerError(response.code(), response.errorBody()?.string()))
            else -> Result.Failure(NetworkError.Unknown(message = response.errorBody()?.string()))
        }

        if (!response.isSuccessful) Log.w(LOG_TAG, "FAIL: $response")

        emit(result)
    }.asSafeNetworkFlow()

    override suspend fun refreshToken(): Flow<Result<Unit>> = flow {
        val refreshToken = authStorage.getRefreshToken() ?: ""

        val response = authService.refreshTokens(RefreshTokensRequestDto(refreshToken))
        val body = response.body()

        val result = when {
            response.isSuccessful && body != null -> Result.Success(Unit)
            response.code() == 401 -> Result.Failure(AuthError.Unauthorized())
            response.code() in 500..599 -> Result.Failure(NetworkError.ServerError(response.code(), response.errorBody()?.string()))
            else -> Result.Failure(NetworkError.Unknown(message = response.errorBody()?.string()))
        }.also {
            if (it is Result.Success && body != null) authStorage.saveTokens(body.accessToken, body.refreshToken)
        }

        if (!response.isSuccessful) Log.w(LOG_TAG, "FAIL: $response")

        emit(result)
    }.asSafeNetworkFlow()

    companion object {
        private const val LOG_TAG = "AuthRepositoryImpl"
    }
}