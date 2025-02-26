package ru.lonelywh1te.introgym.features.auth.data

import android.util.Log
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import retrofit2.Response
import ru.lonelywh1te.introgym.features.auth.data.dto.ConfirmOtpRequestDto
import ru.lonelywh1te.introgym.features.auth.data.dto.ConfirmOtpResponseDto
import ru.lonelywh1te.introgym.features.auth.data.dto.SendOtpRequestDto
import ru.lonelywh1te.introgym.features.auth.data.dto.SendOtpResponseDto
import ru.lonelywh1te.introgym.features.auth.data.dto.RefreshTokensRequestDto
import ru.lonelywh1te.introgym.features.auth.data.dto.RefreshTokensResponseDto
import ru.lonelywh1te.introgym.features.auth.data.dto.SignInRequestDto
import ru.lonelywh1te.introgym.features.auth.data.dto.SignInResponseDto
import ru.lonelywh1te.introgym.features.auth.data.dto.SignUpRequestDto
import ru.lonelywh1te.introgym.features.auth.data.dto.SignUpResponseDto
import ru.lonelywh1te.introgym.features.auth.data.storage.AuthStorage
import ru.lonelywh1te.introgym.features.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthError
import ru.lonelywh1te.introgym.data.network.NetworkError
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
        authService = mockk()
        authStorage = mockk()
        authRepository = AuthRepositoryImpl(authService, authStorage)

        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0

        every { authStorage.getSessionId() } returns sessionId
        every { authStorage.getRefreshToken() } returns refreshToken
        every { authStorage.clearSessionId() } returns Unit
        every { authStorage.clearTokens() } returns Unit
        every { authStorage.saveSessionId(any()) } returns Unit
        every { authStorage.saveTokens(any(), any()) } returns Unit
    }

    @Nested
    inner class SendOtpTests {
        @Test
        fun `sendOtp emit InProgress then Success`() = runTest {
            val requestDto = SendOtpRequestDto(email)
            val responseDto = Response.success(SendOtpResponseDto(otp))

            coEvery { authService.sendOtp(requestDto, otpType) } returns responseDto

            val expected = listOf(Result.InProgress, Result.Success(Unit))
            val actual = authRepository.sendOtp(email, otpType).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `sendOtp emit InProgress and Failure with null body`() = runTest {
            val requestDto = SendOtpRequestDto(email)
            val responseDto = Response.success<SendOtpResponseDto>(200, null)

            coEvery { authService.sendOtp(requestDto, otpType) } returns responseDto

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.sendOtp(email, otpType).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `sendOtp emit InProgress and Failure with http 409`() = runTest {
            val requestDto = SendOtpRequestDto(email)
            val responseDto = Response.error<SendOtpResponseDto>(409, responseBody)

            coEvery { authService.sendOtp(requestDto, otpType) } returns responseDto

            val expected = listOf(Result.InProgress, Result.Failure(AuthError.SESSION_STILL_EXIST))
            val actual = authRepository.sendOtp(email, otpType).toList()


            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `sendOtp emit InProgress and Failure with no internet`() = runTest {
            val requestDto = SendOtpRequestDto(email)

            coEvery { authService.sendOtp(requestDto, otpType) } throws IOException()

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.NO_INTERNET))
            val actual = authRepository.sendOtp(email, otpType).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `sendOtp emit InProgress and Failure with unknown http code`() = runTest {
            val requestDto = SendOtpRequestDto(email)
            val responseDto = Response.error<SendOtpResponseDto>(999, responseBody)

            coEvery { authService.sendOtp(requestDto, otpType) } returns responseDto

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.sendOtp(email, otpType).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `sendOtp emit InProgress and Failure with exception`() = runTest {
            val requestDto = SendOtpRequestDto(email)

            coEvery { authService.sendOtp(requestDto, otpType) } throws Exception()

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

            coEvery { authService.confirmOtp(requestDto, otpType) } returns responseDto

            val expected = listOf(Result.InProgress, Result.Success(Unit))
            val actual = authRepository.confirmOtp(otp, otpType).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `confirmOtp emit InProgress and Failure with isSuccess = false`() = runTest {
            val requestDto = ConfirmOtpRequestDto(sessionId, otp)
            val responseDto = Response.success<ConfirmOtpResponseDto>(200, ConfirmOtpResponseDto(isSuccess = false))

            coEvery { authService.confirmOtp(requestDto, otpType) } returns responseDto

            val expected = listOf(Result.InProgress, Result.Failure(AuthError.INVALID_OTP_CODE))
            val actual = authRepository.confirmOtp(otp, otpType).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `confirmOtp emit InProgress and Failure with no internet connection`() = runTest {
            val requestDto = ConfirmOtpRequestDto(sessionId, otp)

            coEvery { authService.confirmOtp(requestDto, otpType) } throws IOException()

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.NO_INTERNET))
            val actual = authRepository.confirmOtp(otp, otpType).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `confirmOtp emit InProgress and Failure with null body`() = runTest {
            val requestDto = ConfirmOtpRequestDto(sessionId, otp)
            val responseDto = Response.success<ConfirmOtpResponseDto>(200, null)

            coEvery { authService.confirmOtp(requestDto, otpType) } returns responseDto

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.confirmOtp(otp, otpType).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `confirmOtp emit InProgress and Failure with unknown http code`() = runTest {
            val requestDto = ConfirmOtpRequestDto(sessionId, otp)
            val responseDto = Response.error<ConfirmOtpResponseDto>(999, responseBody)

            coEvery { authService.confirmOtp(requestDto, otpType) } returns responseDto

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.confirmOtp(otp, otpType).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `confirmOtp emit InProgress and Failure with exception`() = runTest {
            val requestDto = ConfirmOtpRequestDto(sessionId, otp)

            coEvery { authService.confirmOtp(requestDto, otpType) } throws Exception()

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

            coEvery { authService.signUp(requestDto) } returns responseDto

            val expected = listOf(Result.InProgress, Result.Success(Unit))
            val actual = authRepository.signUp(email, password).toList()

            coVerify(exactly = 1) { authStorage.clearSessionId() }
            coVerify(exactly = 1) { authStorage.saveTokens(accessToken, refreshToken) }
            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signUp emit InProgress and Failure with http code 400`() = runTest {
            val requestDto = SignUpRequestDto(sessionId, email, password)
            val responseDto = Response.error<SignUpResponseDto>(400, responseBody)

            coEvery { authService.signUp(requestDto) } returns responseDto

            val expected = listOf(Result.InProgress, Result.Failure(AuthError.INVALID_SESSION))
            val actual = authRepository.signUp(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signUp emit InProgress and Failure with http code 409`() = runTest {
            val requestDto = SignUpRequestDto(sessionId, email, password)
            val responseDto = Response.error<SignUpResponseDto>(409, responseBody)

            coEvery { authService.signUp(requestDto) } returns responseDto

            val expected = listOf(Result.InProgress, Result.Failure(AuthError.EMAIL_ALREADY_REGISTERED))
            val actual = authRepository.signUp(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signUp emit InProgress and Failure with null body`() = runTest {
            val requestDto = SignUpRequestDto(sessionId, email, password)
            val responseDto = Response.success<SignUpResponseDto>(200, null)

            coEvery { authService.signUp(requestDto) } returns responseDto

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.signUp(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signUp emit InProgress and Failure with unknown http code`() = runTest {
            val requestDto = SignUpRequestDto(sessionId, email, password)
            val responseDto = Response.error<SignUpResponseDto>(999, responseBody)

            coEvery { authService.signUp(requestDto) } returns responseDto

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.signUp(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signUp emit InProgress and Failure with no internet connection`() = runTest {
            val requestDto = SignUpRequestDto(sessionId, email, password)

            coEvery { authService.signUp(requestDto) } throws IOException()

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.NO_INTERNET))
            val actual = authRepository.signUp(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signUp emit InProgress and Failure with exception`() = runTest {
            val requestDto = SignUpRequestDto(sessionId, email, password)

            coEvery { authService.signUp(requestDto) } throws Exception()

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

            coEvery { authService.signIn(requestDto) } returns responseDto

            val expected = listOf(Result.InProgress, Result.Success(Unit))
            val actual = authRepository.signIn(email, password).toList()

            coVerify(exactly = 1) { authStorage.saveTokens(accessToken, refreshToken) }
            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signIn emit InProgress and Failure with http code 400`() = runTest {
            val requestDto = SignInRequestDto(email, password)
            val responseDto = Response.error<SignInResponseDto>(400, responseBody)

            coEvery { authService.signIn(requestDto) } returns responseDto

            val expected = listOf(Result.InProgress, Result.Failure(AuthError.INVALID_EMAIL_OR_PASSWORD))
            val actual = authRepository.signIn(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signIn emit InProgress and Failure with null body`() = runTest {
            val requestDto = SignInRequestDto(email, password)
            val responseDto = Response.success<SignInResponseDto>(200, null)

            coEvery { authService.signIn(requestDto) } returns responseDto

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.signIn(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signIn emit InProgress and Failure with no internet connection`() = runTest {
            val requestDto = SignInRequestDto(email, password)

            coEvery { authService.signIn(requestDto) } throws IOException()

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.NO_INTERNET))
            val actual = authRepository.signIn(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signIn emit InProgress and Failure with unknown http code`() = runTest {
            val requestDto = SignInRequestDto(email, password)
            val responseDto = Response.error<SignInResponseDto>(999, responseBody)

            coEvery { authService.signIn(requestDto) } returns responseDto

            val expected = listOf(Result.InProgress, Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.signIn(email, password).toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `signIn emit InProgress and Failure with exception`() = runTest {
            val requestDto = SignInRequestDto(email, password)

            coEvery { authService.signIn(requestDto) } throws Exception()

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

            coEvery { authService.refreshTokens(requestDto) } returns responseDto

            val expected = listOf(Result.Success(Unit))
            val actual = authRepository.refreshToken().toList()

            coVerify(exactly = 1) { authStorage.saveTokens(accessToken, refreshToken) }
            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `refreshTokens emit Failure with http code 401`() = runTest {
            val requestDto = RefreshTokensRequestDto(refreshToken)
            val responseDto = Response.error<RefreshTokensResponseDto>(401, responseBody)

            coEvery { authService.refreshTokens(requestDto) } returns responseDto

            val expected = listOf(Result.Failure(AuthError.UNAUTHORIZED))
            val actual = authRepository.refreshToken().toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `refreshTokens emit Failure with null body`() = runTest {
            val requestDto = RefreshTokensRequestDto(refreshToken)
            val responseDto = Response.success<RefreshTokensResponseDto>(200, null)

            coEvery { authService.refreshTokens(requestDto) } returns responseDto

            val expected = listOf(Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.refreshToken().toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `refreshTokens emit Failure with no internet connection`() = runTest {
            val requestDto = RefreshTokensRequestDto(refreshToken)

            coEvery { authService.refreshTokens(requestDto) } throws IOException()

            val expected = listOf(Result.Failure(NetworkError.NO_INTERNET))
            val actual = authRepository.refreshToken().toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `refreshTokens emit Failure with unknown http code`() = runTest {
            val requestDto = RefreshTokensRequestDto(refreshToken)
            val responseDto = Response.error<RefreshTokensResponseDto>(888, responseBody)

            coEvery { authService.refreshTokens(requestDto) } returns responseDto

            val expected = listOf(Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.refreshToken().toList()

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `refreshTokens emit Failure with exception`() = runTest {
            val requestDto = RefreshTokensRequestDto(refreshToken)

            coEvery { authService.refreshTokens(requestDto) } throws Exception()

            val expected = listOf(Result.Failure(NetworkError.UNKNOWN))
            val actual = authRepository.refreshToken().toList()

            Assertions.assertEquals(expected, actual)
        }
    }
}