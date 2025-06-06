package ru.lonelywh1te.introgym.features.auth.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.lonelywh1te.introgym.features.auth.data.dto.ChangePasswordRequestDto
import ru.lonelywh1te.introgym.features.auth.data.dto.ChangePasswordResponseDto
import ru.lonelywh1te.introgym.features.auth.data.dto.ConfirmOtpRequestDto
import ru.lonelywh1te.introgym.features.auth.data.dto.ConfirmOtpResponseDto
import ru.lonelywh1te.introgym.features.auth.data.dto.RefreshTokensRequestDto
import ru.lonelywh1te.introgym.features.auth.data.dto.RefreshTokensResponseDto
import ru.lonelywh1te.introgym.features.auth.data.dto.SendOtpRequestDto
import ru.lonelywh1te.introgym.features.auth.data.dto.SendOtpResponseDto
import ru.lonelywh1te.introgym.features.auth.data.dto.SignInRequestDto
import ru.lonelywh1te.introgym.features.auth.data.dto.SignInResponseDto
import ru.lonelywh1te.introgym.features.auth.data.dto.SignUpRequestDto
import ru.lonelywh1te.introgym.features.auth.data.dto.SignUpResponseDto

interface AuthApi {

    @POST("auth/otp/{otpType}/send")
    suspend fun sendOtp(@Body request: SendOtpRequestDto, @Path("otpType") otpType: String): Response<SendOtpResponseDto>

    @POST("auth/otp/{otpType}/confirm")
    suspend fun confirmOtp(@Body request: ConfirmOtpRequestDto, @Path("otpType") otpType: String): Response<ConfirmOtpResponseDto>

    @POST("auth/sign-up")
    suspend fun signUp(@Body request: SignUpRequestDto): Response<SignUpResponseDto>

    @POST("auth/sign-in")
    suspend fun signIn(@Body request: SignInRequestDto): Response<SignInResponseDto>

    @POST("auth/refresh")
    suspend fun refreshTokens(@Body request: RefreshTokensRequestDto): Response<RefreshTokensResponseDto>

    @PUT("auth/password/{sessionId}")
    suspend fun changePassword(@Path("sessionId") sessionId: String, @Body request: ChangePasswordRequestDto): Response<ChangePasswordResponseDto>
}