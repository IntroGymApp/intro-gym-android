package ru.lonelywh1te.introgym.auth.data

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
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
import ru.lonelywh1te.introgym.auth.data.dto.toConfirmOtpResult
import ru.lonelywh1te.introgym.auth.data.dto.toSendOtpResult
import ru.lonelywh1te.introgym.auth.data.dto.toToken
import ru.lonelywh1te.introgym.auth.data.prefs.AuthStorage
import ru.lonelywh1te.introgym.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.auth.domain.error.AuthError
import ru.lonelywh1te.introgym.core.network.NetworkError
import ru.lonelywh1te.introgym.core.result.Result
import java.io.IOException

// TODO: timeout test, server errors

class AuthRepositoryImplTest {
    private lateinit var authService: AuthService
    private lateinit var authStorage: AuthStorage
    private lateinit var authRepository: AuthRepository

    private val otp = "12345"
    private val email = "test@example.com"
    private val password = "test_password"
    private val sessionId = "fb6629c4-53e2-4300-8ee8-0090fb7b562f"
    private val accessToken = "testToken"
    private val refreshToken = "testToken"

    private val responseBody = "{\"message\":\"Some message\"}".toResponseBody("application/json".toMediaType())

    @BeforeEach
    fun setUp() {
        authService = mock()
        authStorage = mock()
        authRepository = AuthRepositoryImpl(authService, authStorage)

        `when`(authStorage.getSessionId()).thenReturn(sessionId)
        `when`(authStorage.getRefreshToken()).thenReturn(refreshToken)
    }

    @Nested
    inner class SendOtpTests {
        @Test
        fun `sendOtp emit InProgress then Success`() = runTest {
            val responseDto = Response.success(SendOtpResponseDto(sessionId))
            val sendOtpResult = responseDto.body()!!.toSendOtpResult()

            `when`(authService.sendOtp(SendOtpRequestDto(email))).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Success(sendOtpResult))
            val actual = authRepository.sendOtp(email).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `sendOtp emit InProgress and Failure with null body`() = runTest {
            val responseDto = Response.success<SendOtpResponseDto>(200, null)

            `when`(authService.sendOtp(SendOtpRequestDto(email))).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.sendOtp(email).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `sendOtp emit InProgress and Failure with http 409`() = runTest {
            val responseDto = Response.error<SendOtpResponseDto>(409, responseBody)

            `when`(authService.sendOtp(SendOtpRequestDto(email))).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(AuthError.SESSION_STILL_EXIST))
            val actual = authRepository.sendOtp(email).toList()


            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `sendOtp emit InProgress and Failure with no internet`() = runTest {
            `when`(authService.sendOtp(any())).doAnswer { throw IOException() }

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.NO_INTERNET))
            val actual = authRepository.sendOtp(email).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `sendOtp emit InProgress and Failure with unknown http code`() = runTest {
            val responseDto = Response.error<SendOtpResponseDto>(999, responseBody)

            `when`(authService.sendOtp(SendOtpRequestDto(email))).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.sendOtp(email).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `sendOtp emit InProgress and Failure with exception`() = runTest {
            `when`(authService.sendOtp(any())).doAnswer { throw Exception() }

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.sendOtp(email).toList()

            Assertions.assertEquals(expected, actual)
        }
    }

    @Nested
    inner class ConfirmOtpTests {
        @Test
        fun `confirmOtp emit InProgress and Success`() = runTest {
            val responseDto = Response.success<ConfirmOtpResponseDto>(200, ConfirmOtpResponseDto(isSuccess = true))
            val responseDtoResult = responseDto.body()!!.toConfirmOtpResult()

            `when`(authStorage.getSessionId()).thenReturn(sessionId)
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
            `when`(authService.confirmOtp(any())).doAnswer { throw IOException() }

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.NO_INTERNET))
            val actual = authRepository.confirmOtp(otp).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `confirmOtp emit InProgress and Failure with null body`() = runTest {
            val responseDto = Response.success<ConfirmOtpResponseDto>(200, null)

            `when`(authService.confirmOtp(ConfirmOtpRequestDto(sessionId, otp))).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.sendOtp(email).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `confirmOtp emit InProgress and Failure with unknown http code`() = runTest {
            val responseDto = Response.error<ConfirmOtpResponseDto>(999, responseBody)

            `when`(authService.confirmOtp(ConfirmOtpRequestDto(sessionId, otp))).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.confirmOtp(otp).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `confirmOtp emit InProgress and Failure with exception`() = runTest {
            `when`(authService.confirmOtp(any())).doAnswer { throw Exception() }

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.confirmOtp(otp).toList()

            Assertions.assertEquals(expected, actual)
        }
    }

    @Nested
    inner class SignUpTests {
        @Test
        fun `signUp emit InProgress and Success`() = runTest {
            val responseDto = Response.success<SignUpResponseDto>(200, SignUpResponseDto(accessToken, refreshToken))
            val responseDtoResult = responseDto.body()!!

            `when`(authService.signUp(SignUpRequestDto(sessionId, email, password))).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Success(responseDtoResult.toToken()))
            val actual = authRepository.signUp(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signUp emit InProgress and Failure with http code 400`() = runTest {
            val responseDto = Response.error<SignUpResponseDto>(400, responseBody)

            `when`(authService.signUp(SignUpRequestDto(sessionId, email, password))).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(AuthError.SESSION_TIMEOUT))
            val actual = authRepository.signUp(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signUp emit InProgress and Failure with http code 409`() = runTest {
            val responseDto = Response.error<SignUpResponseDto>(409, responseBody)

            `when`(authService.signUp(SignUpRequestDto(sessionId, email, password))).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(AuthError.EMAIL_ALREADY_REGISTERED))
            val actual = authRepository.signUp(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signUp emit InProgress and Failure with null body`() = runTest {
            val responseDto = Response.success<SignUpResponseDto>(200, null)

            `when`(authService.signUp(SignUpRequestDto(sessionId, email, password))).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.signUp(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signUp emit InProgress and Failure with unknown http code`() = runTest {
            val responseDto = Response.error<SignUpResponseDto>(999, responseBody)

            `when`(authService.signUp(SignUpRequestDto(sessionId, email, password))).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.signUp(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signUp emit InProgress and Failure with no internet connection`() = runTest {
            `when`(authService.signUp(any())).doAnswer { throw IOException() }

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.NO_INTERNET))
            val actual = authRepository.signUp(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signUp emit InProgress and Failure with exception`() = runTest {
            `when`(authService.sendOtp(any())).doAnswer { throw Exception() }

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.signUp(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }
    }

    @Nested
    inner class SignInTests {
        @Test
        fun `signIn emit InProgress and Success`() = runTest {
            val responseDto = Response.success<SignInResponseDto>(SignInResponseDto(accessToken, refreshToken))
            val responseDtoResult = responseDto.body()!!

            `when`(authService.signIn(SignInRequestDto(email, password))).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Success(responseDtoResult.toToken()))
            val actual = authRepository.signIn(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signIn emit InProgress and Failure with http code 400`() = runTest {
            val responseDto = Response.error<SignInResponseDto>(400, responseBody)

            `when`(authService.signIn(SignInRequestDto(email, password))).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(AuthError.INVALID_EMAIL_OR_PASSWORD))
            val actual = authRepository.signIn(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signIn emit InProgress and Failure with null body`() = runTest {
            val responseDto = Response.success<SignInResponseDto>(200, null)

            `when`(authService.signIn(SignInRequestDto(email, password))).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.signIn(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signIn emit InProgress and Failure with no internet connection`() = runTest {
            `when`(authService.signIn(any())).doAnswer { throw IOException() }

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.NO_INTERNET))
            val actual = authRepository.signIn(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signIn emit InProgress and Failure with unknown http code`() = runTest {
            val responseDto = Response.error<SignInResponseDto>(999, responseBody)

            `when`(authService.signIn(SignInRequestDto(email, password))).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.signIn(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signIn emit InProgress and Failure with exception`() = runTest {
            `when`(authService.sendOtp(any())).doAnswer { throw Exception() }

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.signIn(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }
    }

    @Nested
    inner class RefreshTokenTests {
        @Test
        fun `refreshTokens emit Success`() = runTest {
            val responseDto = Response.success<RefreshTokensResponseDto>(RefreshTokensResponseDto(accessToken, refreshToken))
            val responseDtoResult = responseDto.body()!!

            `when`(authService.refreshTokens(RefreshTokensRequestDto(refreshToken))).thenReturn(responseDto)

            val expected = listOf(Result.Success(responseDtoResult.toToken()))
            val actual = authRepository.refreshToken().toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `refreshTokens emit Failure with http code 401`() = runTest {
            val responseDto = Response.error<RefreshTokensResponseDto>(401, responseBody)

            `when`(authService.refreshTokens(RefreshTokensRequestDto(refreshToken))).thenReturn(responseDto)

            val expected = listOf(Result.Failure(AuthError.UNAUTHORIZED))
            val actual = authRepository.refreshToken().toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `refreshTokens emit Failure with null body`() = runTest {
            val responseDto = Response.success<RefreshTokensResponseDto>(200, null)

            `when`(authService.refreshTokens(RefreshTokensRequestDto(refreshToken))).thenReturn(responseDto)

            val expected = listOf(Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.refreshToken().toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `refreshTokens emit Failure with no internet connection`() = runTest {
            `when`(authStorage.getRefreshToken()).thenReturn(refreshToken)
            `when`(authService.refreshTokens(any())).doAnswer { throw IOException() }

            val expected = listOf(Result.Failure(NetworkError.NO_INTERNET))
            val actual = authRepository.refreshToken().toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `refreshTokens emit Failure with unknown http code`() = runTest {
            val responseDto = Response.error<RefreshTokensResponseDto>(888, responseBody)
            
            `when`(authService.refreshTokens(RefreshTokensRequestDto(refreshToken))).thenReturn(responseDto)

            val expected = listOf(Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.refreshToken().toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `refreshTokens emit Failure with exception`() = runTest {
            `when`(authStorage.getRefreshToken()).thenReturn(refreshToken)
            `when`(authService.refreshTokens(any())).doAnswer { throw Exception() }

            val expected = listOf(Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.refreshToken().toList()

            Assertions.assertEquals(expected, actual)
        }
    }
}