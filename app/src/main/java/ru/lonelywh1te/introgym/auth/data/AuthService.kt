package ru.lonelywh1te.introgym.auth.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import ru.lonelywh1te.introgym.auth.data.dto.otp_confirm.ConfirmOtpRequestDto
import ru.lonelywh1te.introgym.auth.data.dto.otp_confirm.ConfirmOtpResponseDto
import ru.lonelywh1te.introgym.auth.data.dto.otp_send.SendOtpRequestDto
import ru.lonelywh1te.introgym.auth.data.dto.otp_send.SendOtpResponseDto
import ru.lonelywh1te.introgym.auth.data.dto.refresh_tokens.RefreshTokensRequestDto
import ru.lonelywh1te.introgym.auth.data.dto.refresh_tokens.RefreshTokensResponseDto
import ru.lonelywh1te.introgym.auth.data.dto.sign_in.SignInRequestDto
import ru.lonelywh1te.introgym.auth.data.dto.sign_in.SignInResponseDto
import ru.lonelywh1te.introgym.auth.data.dto.sign_up.SignUpRequestDto
import ru.lonelywh1te.introgym.auth.data.dto.sign_up.SignUpResponseDto

interface AuthService {

    @POST("auth/otp/send")
    suspend fun sendOtp(@Body request: SendOtpRequestDto): Response<SendOtpResponseDto>

    @POST("auth/otp/confirm")
    suspend fun confirmOtp(@Body request: ConfirmOtpRequestDto): Response<ConfirmOtpResponseDto>

    @POST("auth/sign-up")
    suspend fun signUp(@Body request: SignUpRequestDto): Response<SignUpResponseDto>

    @POST("auth/sign-in")
    suspend fun signIn(@Body request: SignInRequestDto): Response<SignInResponseDto>

    @POST("auth/refresh")
    suspend fun refreshTokens(@Body request: RefreshTokensRequestDto): Response<RefreshTokensResponseDto>

}