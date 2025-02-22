package ru.lonelywh1te.introgym.auth.data

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import retrofit2.Response
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
import ru.lonelywh1te.introgym.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.auth.domain.error.AuthError
import ru.lonelywh1te.introgym.core.network.NetworkError
import ru.lonelywh1te.introgym.core.result.Result
import java.io.IOException

// TODO: timeout test, server errors

class AuthRepositoryImplTest {
    private lateinit var authService: AuthService
    private lateinit var authRepository: AuthRepository

    private val otp = "12345"
    private val email = "test@example.com"
    private val password = "test_password"
    private val sessionId = "fb6629c4-53e2-4300-8ee8-0090fb7b562f"
    private val accessToken = "testToken"
    private val refreshToken = "testToken"

    @BeforeEach
    fun setUp() {
        authService = mock()
        authRepository = AuthRepositoryImpl(authService)
    }

    @Test
    fun `sendOtp emit InProgress then Success`() = runTest {
        val responseDto = Response.success(SendOtpResponseDto(sessionId))
        val sendOtpResult = responseDto.body()!!.sessionId

        `when`(authService.sendOtp(SendOtpRequestDto(email))).thenReturn(responseDto)

        val expected = listOf(Result.InProgress, Result.Success(sendOtpResult))
        val actual = authRepository.sendOtp(email).toList()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `sendOtp emit InProgress and Failure with http 409`() = runTest {
        val responseDto = Response.error<SendOtpResponseDto>(409, ResponseBody.create(MediaType.get("application/json"), ""))

        `when`(authService.sendOtp(SendOtpRequestDto(email))).thenReturn(responseDto)

        val expected = listOf(Result.InProgress, Result.Failure(AuthError.SESSION_STILL_EXIST))
        val actual = authRepository.sendOtp(email).toList()


        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `sendOtp emit InProgress and Failure with no internet`() = runTest {
        `when`(authService.sendOtp(SendOtpRequestDto(email))).thenThrow(IOException())

        val expected = listOf(Result.Failure(NetworkError.NO_INTERNET))
        val actual = authRepository.sendOtp(email).toList()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `sendOtp emit InProgress and Failure with unknown http code`() = runTest {
        val responseDto = Response.error<SendOtpResponseDto>(999, ResponseBody.create(MediaType.get("application/json"), ""))

        `when`(authService.sendOtp(SendOtpRequestDto(email))).thenReturn(responseDto)

        val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
        val actual = authRepository.sendOtp(email).toList()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `confirmOtp emit InProgress and Success`() = runTest {
        val responseDto = Response.success<ConfirmOtpResponseDto>(200, ConfirmOtpResponseDto(isSuccess = true))
        val responseDtoResult = responseDto.body()!!

        `when`(authService.confirmOtp(ConfirmOtpRequestDto(sessionId, otp))).thenReturn(responseDto)

        val expected = listOf(Result.InProgress, Result.Success(responseDtoResult))
        val actual = authRepository.confirmOtp(otp).toList()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `confirmOtp emit InProgress and Failure with isSuccess = false`() = runTest {
        val responseDto = Response.success<ConfirmOtpResponseDto>(200, ConfirmOtpResponseDto(isSuccess = false))

        `when`(authService.confirmOtp(ConfirmOtpRequestDto(sessionId, otp))).thenReturn(responseDto)

        val expected = listOf(Result.InProgress, Result.Failure(AuthError.INVALID_OTP_CODE))
        val actual = authRepository.confirmOtp(otp).toList()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `confirmOtp emit InProgress and Failure with no internet connection`() = runTest {
        `when`(authService.confirmOtp(ConfirmOtpRequestDto(sessionId, otp))).thenThrow(IOException())

        val expected = listOf(Result.Failure(NetworkError.NO_INTERNET))
        val actual = authRepository.confirmOtp(otp).toList()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `confirmOtp emit InProgress and Failure with unknown http code`() = runTest {
        val responseDto = Response.error<ConfirmOtpResponseDto>(999, ResponseBody.create(MediaType.get("application/json"), ""))

        `when`(authService.confirmOtp(ConfirmOtpRequestDto(sessionId, otp))).thenReturn(responseDto)

        val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
        val actual = authRepository.confirmOtp(otp).toList()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `signUp emit InProgress and Success`() = runTest {
        val responseDto = Response.success<SignUpResponseDto>(200, SignUpResponseDto(accessToken, refreshToken))
        val responseDtoResult = responseDto.body()!!

        `when`(authService.signUp(SignUpRequestDto(sessionId, email, password))).thenReturn(responseDto)

        val expected = listOf(Result.InProgress, Result.Success(responseDtoResult))
        val actual = authRepository.signUp(email, password).toList()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `signUp emit InProgress and Failure with http code 400`() = runTest {
        val responseDto = Response.error<SignUpResponseDto>(400, ResponseBody.create(MediaType.get("application/json"), ""))

        `when`(authService.signUp(SignUpRequestDto(sessionId, email, password))).thenReturn(responseDto)

        val expected = listOf(Result.InProgress, Result.Failure(AuthError.SESSION_TIMEOUT))
        val actual = authRepository.signUp(email, password).toList()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `signUp emit InProgress and Failure with http code 409`() = runTest {
        val responseDto = Response.error<SignUpResponseDto>(409, ResponseBody.create(MediaType.get("application/json"), ""))

        `when`(authService.signUp(SignUpRequestDto(sessionId, email, password))).thenReturn(responseDto)

        val expected = listOf(Result.InProgress, Result.Failure(AuthError.EMAIL_ALREADY_REGISTERED))
        val actual = authRepository.signUp(email, password).toList()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `signUp emit InProgress and Failure with unknown http code`() = runTest {
        val responseDto = Response.error<SignUpResponseDto>(999, ResponseBody.create(MediaType.get("application/json"), ""))

        `when`(authService.signUp(SignUpRequestDto(sessionId, email, password))).thenReturn(responseDto)

        val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
        val actual = authRepository.signUp(email, password).toList()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `signIn emit InProgress and Success`() = runTest {
        val responseDto = Response.success<SignInResponseDto>(SignInResponseDto(accessToken, refreshToken))
        val responseDtoResult = responseDto.body()!!

        `when`(authService.signIn(SignInRequestDto(email, password))).thenReturn(responseDto)

        val expected = listOf(Result.InProgress, Result.Success(responseDtoResult))
        val actual = authRepository.signIn(email, password).toList()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `signIn emit InProgress and Failure with http code 400`() = runTest {
        val responseDto = Response.error<SignInResponseDto>(400, ResponseBody.create(MediaType.get("application/json"), ""))

        `when`(authService.signIn(SignInRequestDto(email, password))).thenReturn(responseDto)

        val expected = listOf(Result.InProgress, Result.Failure(AuthError.INVALID_EMAIL_OR_PASSWORD))
        val actual = authRepository.signIn(email, password).toList()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `signIn emit InProgress and Failure with no internet connection`() = runTest {
        `when`(authService.signIn(SignInRequestDto(email, password))).thenThrow(IOException())

        val expected = listOf(Result.InProgress, Result.Failure(NetworkError.NO_INTERNET))
        val actual = authRepository.signIn(email, password).toList()

        Assertions.assertEquals(expected, actual)
    }


    @Test
    fun `signIn emit InProgress and Failure with unknown http code`() = runTest {
        val responseDto = Response.error<SignInResponseDto>(999, ResponseBody.create(MediaType.get("application/json"), ""))

        `when`(authService.signIn(SignInRequestDto(email, password))).thenReturn(responseDto)

        val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
        val actual = authRepository.signIn(email, password).toList()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `refreshTokens emit Success`() = runTest {
        val responseDto = Response.success<RefreshTokensResponseDto>(RefreshTokensResponseDto(accessToken, refreshToken))
        val responseDtoResult = responseDto.body()!!

        `when`(authService.refreshTokens(RefreshTokensRequestDto(refreshToken))).thenReturn(responseDto)

        val expected = listOf(Result.Success(responseDtoResult))
        val actual = authRepository.refreshToken(refreshToken).toList()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `refreshTokens emit Failure with http code 401`() = runTest {
        val responseDto = Response.error<RefreshTokensResponseDto>(401, ResponseBody.create(MediaType.get("application/json"), ""))

        `when`(authService.refreshTokens(RefreshTokensRequestDto(refreshToken))).thenReturn(responseDto)

        val expected = listOf(Result.Failure(AuthError.UNAUTHORIZED))
        val actual = authRepository.refreshToken(refreshToken).toList()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `refreshTokens emit Failure with no internet connection`() = runTest {
        `when`(authService.refreshTokens(RefreshTokensRequestDto(refreshToken))).thenThrow(IOException())

        val expected = listOf(Result.Failure(NetworkError.NO_INTERNET))
        val actual = authRepository.refreshToken(refreshToken).toList()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `refreshTokens emit Failure with unknown http code`() = runTest {
        val responseDto = Response.error<RefreshTokensResponseDto>(999, ResponseBody.create(MediaType.get("application/json"), ""))

        `when`(authService.refreshTokens(RefreshTokensRequestDto(refreshToken))).thenReturn(responseDto)

        val expected = listOf(Result.Failure(NetworkError.UNKNOWN))
        val actual = authRepository.refreshToken(refreshToken).toList()

        Assertions.assertEquals(expected, actual)
    }
}