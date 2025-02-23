package ru.lonelywh1te.introgym.auth.domain.usecase

import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.lonelywh1te.introgym.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.auth.domain.error.ValidationError
import ru.lonelywh1te.introgym.auth.domain.model.OtpType
import ru.lonelywh1te.introgym.auth.domain.validator.EmailPasswordValidator
import ru.lonelywh1te.introgym.core.result.Result

class SendOtpUseCaseTest {
    private lateinit var authRepository: AuthRepository
    private lateinit var useCase: SendOtpUseCase
    private val email = "invalid_email"
    private val otpType = OtpType.CONFIRM_SIGNUP

    @BeforeEach
    fun setUp() {
        mockkObject(EmailPasswordValidator)
        authRepository = mockk()
        useCase = SendOtpUseCase(authRepository)
    }

    @Test
    fun `sendOtp with validate failure`() = runTest {
        every { EmailPasswordValidator.validate(email) } returns Result.Failure(ValidationError.INVALID_EMAIL_FORMAT)
        val expected = listOf(Result.Failure(ValidationError.INVALID_EMAIL_FORMAT))
        val actual = useCase(email, otpType).toList()

        coVerify { authRepository wasNot Called }

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `sendOtp with validate success`() = runTest {
        every { EmailPasswordValidator.validate(email) } returns Result.Success(Unit)
        coEvery { authRepository.sendOtp(email, otpType.name) } returns flowOf(Result.InProgress, Result.Success(Unit))

        val expected = listOf(Result.InProgress, Result.Success(Unit))
        val actual = useCase(email, otpType).toList()

        Assertions.assertEquals(expected, actual)
        coVerify(exactly = 1) { authRepository.sendOtp(email, otpType.name) }
    }
}