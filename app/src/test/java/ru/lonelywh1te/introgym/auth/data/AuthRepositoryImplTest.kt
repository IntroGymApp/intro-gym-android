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
    private val otpType = "ANY_TYPE"
    private val email = "test@example.com"
    private val password = "test_password"
    private val sessionId = "any_uuid"
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
            val requestDto = SendOtpRequestDto(email)
            val responseDto = Response.success(SendOtpResponseDto(otp))

            `when`(authService.sendOtp(requestDto, otpType)).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Success(Unit))
            val actual = authRepository.sendOtp(email, otpType).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `sendOtp emit InProgress and Failure with null body`() = runTest {
            val requestDto = SendOtpRequestDto(email)
            val responseDto = Response.success<SendOtpResponseDto>(200, null)

            `when`(authService.sendOtp(requestDto, otpType)).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.sendOtp(email, otpType).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `sendOtp emit InProgress and Failure with http 409`() = runTest {
            val requestDto = SendOtpRequestDto(email)
            val responseDto = Response.error<SendOtpResponseDto>(409, responseBody)

            `when`(authService.sendOtp(requestDto, otpType)).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(AuthError.SESSION_STILL_EXIST))
            val actual = authRepository.sendOtp(email, otpType).toList()


            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `sendOtp emit InProgress and Failure with no internet`() = runTest {
            val requestDto = SendOtpRequestDto(email)

            `when`(authService.sendOtp(requestDto, otpType)).doAnswer { throw IOException() }

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.NO_INTERNET))
            val actual = authRepository.sendOtp(email, otpType).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `sendOtp emit InProgress and Failure with unknown http code`() = runTest {
            val requestDto = SendOtpRequestDto(email)
            val responseDto = Response.error<SendOtpResponseDto>(999, responseBody)

            `when`(authService.sendOtp(requestDto, otpType)).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.sendOtp(email, otpType).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `sendOtp emit InProgress and Failure with exception`() = runTest {
            val requestDto = SendOtpRequestDto(email)

            `when`(authService.sendOtp(requestDto, otpType)).doAnswer { throw Exception() }

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.sendOtp(email, otpType).toList()

            Assertions.assertEquals(expected, actual)
        }
    }

    @Nested
    inner class ConfirmOtpTests {
        @Test
        fun `confirmOtp emit InProgress and Success`() = runTest {
            val requestDto = ConfirmOtpRequestDto(sessionId, otp)
            val responseDto = Response.success<ConfirmOtpResponseDto>(200, ConfirmOtpResponseDto(isSuccess = true))

            `when`(authService.confirmOtp(requestDto, otpType)).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Success(Unit))
            val actual = authRepository.confirmOtp(otp, otpType).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `confirmOtp emit InProgress and Failure with isSuccess = false`() = runTest {
            val requestDto = ConfirmOtpRequestDto(sessionId, otp)
            val responseDto = Response.success<ConfirmOtpResponseDto>(200, ConfirmOtpResponseDto(isSuccess = false))

            `when`(authService.confirmOtp(requestDto, otpType)).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(AuthError.INVALID_OTP_CODE))
            val actual = authRepository.confirmOtp(otp, otpType).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `confirmOtp emit InProgress and Failure with no internet connection`() = runTest {
            val requestDto = ConfirmOtpRequestDto(sessionId, otp)

            `when`(authService.confirmOtp(requestDto, otpType)).doAnswer { throw IOException() }

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.NO_INTERNET))
            val actual = authRepository.confirmOtp(otp, otpType).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `confirmOtp emit InProgress and Failure with null body`() = runTest {
            val requestDto = ConfirmOtpRequestDto(sessionId, otp)
            val responseDto = Response.success<ConfirmOtpResponseDto>(200, null)

            `when`(authService.confirmOtp(requestDto, otpType)).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.confirmOtp(otp, otpType).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `confirmOtp emit InProgress and Failure with unknown http code`() = runTest {
            val requestDto = ConfirmOtpRequestDto(sessionId, otp)
            val responseDto = Response.error<ConfirmOtpResponseDto>(999, responseBody)

            `when`(authService.confirmOtp(requestDto, otpType)).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.confirmOtp(otp, otpType).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `confirmOtp emit InProgress and Failure with exception`() = runTest {
            val requestDto = ConfirmOtpRequestDto(sessionId, otp)

            `when`(authService.confirmOtp(requestDto, otpType)).doAnswer { throw Exception() }

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.confirmOtp(otp, otpType).toList()

            Assertions.assertEquals(expected, actual)
        }
    }

    @Nested
    inner class SignUpTests {
        @Test
        fun `signUp emit InProgress and Success`() = runTest {
            val requestDto = SignUpRequestDto(sessionId, email, password)
            val responseDto = Response.success<SignUpResponseDto>(200, SignUpResponseDto(accessToken, refreshToken))

            `when`(authService.signUp(requestDto)).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Success(Unit))
            val actual = authRepository.signUp(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signUp emit InProgress and Failure with http code 400`() = runTest {
            val requestDto = SignUpRequestDto(sessionId, email, password)
            val responseDto = Response.error<SignUpResponseDto>(400, responseBody)

            `when`(authService.signUp(requestDto)).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(AuthError.SESSION_TIMEOUT))
            val actual = authRepository.signUp(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signUp emit InProgress and Failure with http code 409`() = runTest {
            val requestDto = SignUpRequestDto(sessionId, email, password)
            val responseDto = Response.error<SignUpResponseDto>(409, responseBody)

            `when`(authService.signUp(requestDto)).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(AuthError.EMAIL_ALREADY_REGISTERED))
            val actual = authRepository.signUp(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signUp emit InProgress and Failure with null body`() = runTest {
            val requestDto = SignUpRequestDto(sessionId, email, password)
            val responseDto = Response.success<SignUpResponseDto>(200, null)

            `when`(authService.signUp(requestDto)).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.signUp(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signUp emit InProgress and Failure with unknown http code`() = runTest {
            val requestDto = SignUpRequestDto(sessionId, email, password)
            val responseDto = Response.error<SignUpResponseDto>(999, responseBody)

            `when`(authService.signUp(requestDto)).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.signUp(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signUp emit InProgress and Failure with no internet connection`() = runTest {
            val requestDto = SignUpRequestDto(sessionId, email, password)

            `when`(authService.signUp(requestDto)).doAnswer { throw IOException() }

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.NO_INTERNET))
            val actual = authRepository.signUp(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signUp emit InProgress and Failure with exception`() = runTest {
            val requestDto = SignUpRequestDto(sessionId, email, password)

            `when`(authService.signUp(requestDto)).doAnswer { throw Exception() }

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.signUp(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }
    }

    @Nested
    inner class SignInTests {
        @Test
        fun `signIn emit InProgress and Success`() = runTest {
            val requestDto = SignInRequestDto(email, password)
            val responseDto = Response.success<SignInResponseDto>(SignInResponseDto(accessToken, refreshToken))

            `when`(authService.signIn(requestDto)).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Success(Unit))
            val actual = authRepository.signIn(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signIn emit InProgress and Failure with http code 400`() = runTest {
            val requestDto = SignInRequestDto(email, password)
            val responseDto = Response.error<SignInResponseDto>(400, responseBody)

            `when`(authService.signIn(requestDto)).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(AuthError.INVALID_EMAIL_OR_PASSWORD))
            val actual = authRepository.signIn(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signIn emit InProgress and Failure with null body`() = runTest {
            val requestDto = SignInRequestDto(email, password)
            val responseDto = Response.success<SignInResponseDto>(200, null)

            `when`(authService.signIn(requestDto)).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.signIn(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signIn emit InProgress and Failure with no internet connection`() = runTest {
            val requestDto = SignInRequestDto(email, password)

            `when`(authService.signIn(requestDto)).doAnswer { throw IOException() }

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.NO_INTERNET))
            val actual = authRepository.signIn(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signIn emit InProgress and Failure with unknown http code`() = runTest {
            val requestDto = SignInRequestDto(email, password)
            val responseDto = Response.error<SignInResponseDto>(999, responseBody)

            `when`(authService.signIn(requestDto)).thenReturn(responseDto)

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.signIn(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signIn emit InProgress and Failure with exception`() = runTest {
            val requestDto = SignInRequestDto(email, password)

            `when`(authService.signIn(requestDto)).doAnswer { throw Exception() }

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.signIn(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }
    }

    @Nested
    inner class RefreshTokenTests {
        @Test
        fun `refreshTokens emit Success`() = runTest {
            val requestDto = RefreshTokensRequestDto(refreshToken)
            val responseDto = Response.success<RefreshTokensResponseDto>(RefreshTokensResponseDto(accessToken, refreshToken))

            `when`(authService.refreshTokens(requestDto)).thenReturn(responseDto)

            val expected = listOf(Result.Success(Unit))
            val actual = authRepository.refreshToken().toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `refreshTokens emit Failure with http code 401`() = runTest {
            val requestDto = RefreshTokensRequestDto(refreshToken)
            val responseDto = Response.error<RefreshTokensResponseDto>(401, responseBody)

            `when`(authService.refreshTokens(requestDto)).thenReturn(responseDto)

            val expected = listOf(Result.Failure(AuthError.UNAUTHORIZED))
            val actual = authRepository.refreshToken().toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `refreshTokens emit Failure with null body`() = runTest {
            val requestDto = RefreshTokensRequestDto(refreshToken)
            val responseDto = Response.success<RefreshTokensResponseDto>(200, null)

            `when`(authService.refreshTokens(requestDto)).thenReturn(responseDto)

            val expected = listOf(Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.refreshToken().toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `refreshTokens emit Failure with no internet connection`() = runTest {
            val requestDto = RefreshTokensRequestDto(refreshToken)

            `when`(authService.refreshTokens(requestDto)).doAnswer { throw IOException() }

            val expected = listOf(Result.Failure(NetworkError.NO_INTERNET))
            val actual = authRepository.refreshToken().toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `refreshTokens emit Failure with unknown http code`() = runTest {
            val requestDto = RefreshTokensRequestDto(refreshToken)
            val responseDto = Response.error<RefreshTokensResponseDto>(888, responseBody)
            
            `when`(authService.refreshTokens(requestDto)).thenReturn(responseDto)

            val expected = listOf(Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.refreshToken().toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `refreshTokens emit Failure with exception`() = runTest {
            val requestDto = RefreshTokensRequestDto(refreshToken)

            `when`(authService.refreshTokens(requestDto)).doAnswer { throw Exception() }

            val expected = listOf(Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.refreshToken().toList()

            Assertions.assertEquals(expected, actual)
        }
    }
}