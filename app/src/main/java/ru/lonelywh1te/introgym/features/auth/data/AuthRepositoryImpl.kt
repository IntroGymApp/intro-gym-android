package ru.lonelywh1te.introgym.features.auth.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.lonelywh1te.introgym.features.auth.data.dto.ConfirmOtpRequestDto
import ru.lonelywh1te.introgym.features.auth.data.dto.SendOtpRequestDto
import ru.lonelywh1te.introgym.features.auth.data.dto.RefreshTokensRequestDto
import ru.lonelywh1te.introgym.features.auth.data.dto.SignInRequestDto
import ru.lonelywh1te.introgym.features.auth.data.dto.SignUpRequestDto
import ru.lonelywh1te.introgym.features.auth.data.storage.AuthStorage
import ru.lonelywh1te.introgym.features.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthError
import ru.lonelywh1te.introgym.data.network.NetworkError
import ru.lonelywh1te.introgym.core.result.Result
import java.io.IOException

class AuthRepositoryImpl(private val authService: AuthService, private val authStorage: AuthStorage):
    AuthRepository {
    override suspend fun sendOtp(email: String, otpType: String): Flow<Result<Unit>> = flow {
        try {
            emit(Result.InProgress)
            val request = authService.sendOtp(SendOtpRequestDto(email), otpType)
            val body = request.body()

            when {
                request.isSuccessful && body != null -> {
                    emit(Result.Success(Unit))
                    authStorage.saveSessionId(body.sessionId)
                }
                request.code() == 409 -> emit(Result.Failure(AuthError.SESSION_STILL_EXIST))
                else -> {
                    emit(Result.Failure(NetworkError.UNKNOWN))
                    Log.w(LOG_TAG, "FAIL: $request")
                }
            }

        } catch (e: IOException) {
            emit(Result.Failure(NetworkError.NO_INTERNET))
        } catch (e: Exception) {
            emit(Result.Failure(NetworkError.UNKNOWN))
            Log.e(LOG_TAG, "UNKNOWN EXCEPTION: $e")
        }
    }

    override suspend fun confirmOtp(otp: String, otpType: String): Flow<Result<Unit>> = flow {
        val sessionId = authStorage.getSessionId() ?: ""

        try {
            emit(Result.InProgress)
            val request = authService.confirmOtp(ConfirmOtpRequestDto(sessionId, otp), otpType)
            val body = request.body()

            when {
                request.isSuccessful && body != null && body.isSuccess -> emit(Result.Success(Unit))
                request.isSuccessful && body != null && !body.isSuccess -> emit(Result.Failure(AuthError.INVALID_OTP_CODE))
                request.code() == 400 -> emit(Result.Failure(AuthError.INVALID_SESSION))
                else -> {
                    emit(Result.Failure(NetworkError.UNKNOWN))
                    Log.w(LOG_TAG, "FAIL: $request")
                }
            }

        } catch (e: IOException) {
            emit(Result.Failure(NetworkError.NO_INTERNET))
        } catch (e: Exception) {
            emit(Result.Failure(NetworkError.UNKNOWN))
            Log.e(LOG_TAG, "UNKNOWN EXCEPTION: $e")
        }
    }

    override suspend fun signUp(email: String, password: String): Flow<Result<Unit>> = flow {
        val sessionId = authStorage.getSessionId() ?: ""

        try {
            emit(Result.InProgress)
            val request = authService.signUp(SignUpRequestDto(sessionId, email, password))
            val body = request.body()

            when {
                request.isSuccessful && body != null -> {
                    emit(Result.Success(Unit))
                    authStorage.clearSessionId()
                    authStorage.saveTokens(body.accessToken, body.refreshToken)
                }
                request.code() == 409 -> emit(Result.Failure(AuthError.EMAIL_ALREADY_REGISTERED))
                request.code() == 400 -> emit(Result.Failure(AuthError.INVALID_SESSION))
                else -> {
                    emit(Result.Failure(NetworkError.UNKNOWN))
                    Log.w(LOG_TAG, "FAIL: $request")
                }
            }

        } catch (e: IOException) {
            emit(Result.Failure(NetworkError.NO_INTERNET))
        } catch (e: Exception) {
            emit(Result.Failure(NetworkError.UNKNOWN))
            Log.e(LOG_TAG, "UNKNOWN EXCEPTION: $e")
        }
    }

    override suspend fun signIn(email: String, password: String): Flow<Result<Unit>> = flow {
        try {
            emit(Result.InProgress)
            val request = authService.signIn(SignInRequestDto(email, password))
            val body = request.body()

            when {
                request.isSuccessful && body != null -> {
                    emit(Result.Success(Unit))
                    authStorage.saveTokens(body.accessToken, body.refreshToken)
                }
                request.code() == 400 -> emit(Result.Failure(AuthError.INVALID_EMAIL_OR_PASSWORD))
                else -> {
                    emit(Result.Failure(NetworkError.UNKNOWN))
                    Log.w(LOG_TAG, "FAIL: $request")
                }
            }

        } catch (e: IOException) {
            emit(Result.Failure(NetworkError.NO_INTERNET))
        } catch (e: Exception) {
            emit(Result.Failure(NetworkError.UNKNOWN))
            Log.e(LOG_TAG, "UNKNOWN EXCEPTION: $e")
        }
    }

    override suspend fun refreshToken(): Flow<Result<Unit>> = flow {
        val refreshToken = authStorage.getRefreshToken() ?: ""

        try {
            val request = authService.refreshTokens(RefreshTokensRequestDto(refreshToken))
            val body = request.body()

            when {
                request.isSuccessful && body != null -> {
                    emit(Result.Success(Unit))
                    authStorage.saveTokens(body.accessToken, body.refreshToken)
                }
                request.code() == 401 -> emit(Result.Failure(AuthError.UNAUTHORIZED))
                else -> {
                    emit(Result.Failure(NetworkError.UNKNOWN))
                    Log.w(LOG_TAG, "FAIL: $request")
                }
            }

        } catch (e: IOException) {
            emit(Result.Failure(NetworkError.NO_INTERNET))
        } catch (e: Exception) {
            emit(Result.Failure(NetworkError.UNKNOWN))
            Log.e(LOG_TAG, "UNKNOWN EXCEPTION: $e")
        }
    }

    companion object {
        private const val LOG_TAG = "AuthRepositoryImpl"
    }
}