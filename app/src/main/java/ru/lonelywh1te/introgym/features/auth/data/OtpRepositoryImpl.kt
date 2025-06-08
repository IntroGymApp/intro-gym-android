package ru.lonelywh1te.introgym.features.auth.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.data.network.NetworkError
import ru.lonelywh1te.introgym.data.network.asSafeNetworkFlow
import ru.lonelywh1te.introgym.features.auth.data.dto.ConfirmOtpRequestDto
import ru.lonelywh1te.introgym.features.auth.data.dto.SendOtpRequestDto
import ru.lonelywh1te.introgym.features.auth.data.storage.AuthStorage
import ru.lonelywh1te.introgym.features.auth.domain.OtpRepository
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthError

class OtpRepositoryImpl(
    private val authApi: AuthApi,
    private val authStorage: AuthStorage,
): OtpRepository {
    override fun sendOtp(email: String, otpType: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)

        val response = authApi.sendOtp(SendOtpRequestDto(email), otpType)
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

    override fun confirmOtp(otp: String, otpType: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)

        val sessionId = authStorage.getSessionId() ?: ""

        val response = authApi.confirmOtp(ConfirmOtpRequestDto(sessionId, otp), otpType)
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

    companion object {
        private const val LOG_TAG = "OtpRepositoryImpl"
    }
}