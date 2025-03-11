package ru.lonelywh1te.introgym.features.auth.domain.usecase

import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.features.auth.domain.EmailPasswordValidator
import ru.lonelywh1te.introgym.features.auth.domain.error.ValidationError
import ru.lonelywh1te.introgym.features.auth.domain.model.OtpType

class SendOtpUseCaseTest {
    private lateinit var authRepository: AuthRepository
    private lateinit var validator: EmailPasswordValidator
    private lateinit var useCase: SendOtpUseCase
    private val email = "invalid_email"
    private val otpType = OtpType.CONFIRM_SIGNUP

    @BeforeEach
    fun setUp() {
        authRepository = mockk()
        validator = mockk()
        useCase = SendOtpUseCase(authRepository, validator)
    }

    @Test
    fun `sendOtp with validate failure`() = runTest {
        every { validator.validate(email) } returns Result.Failure(ValidationError.INVALID_EMAIL_FORMAT)
        val expected = listOf(Result.Failure(ValidationError.INVALID_EMAIL_FORMAT))
        val actual = useCase(email, otpType).toList()

        coVerify { authRepository wasNot Called }

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `sendOtp with validate success`() = runTest {
        every { validator.validate(email) } returns Result.Success(Unit)
        coEvery { authRepository.sendOtp(email, otpType.name) } returns flowOf(Result.InProgress, Result.Success(Unit))

        val expected = listOf(Result.InProgress, Result.Success(Unit))
        val actual = useCase(email, otpType).toList()

        Assertions.assertEquals(expected, actual)
        coVerify(exactly = 1) { authRepository.sendOtp(email, otpType.name) }
    }
}