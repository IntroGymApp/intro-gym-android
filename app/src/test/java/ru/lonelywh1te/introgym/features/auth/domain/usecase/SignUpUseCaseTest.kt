package ru.lonelywh1te.introgym.features.auth.domain.usecase

import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkObject
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.features.auth.domain.EmailPasswordValidator
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthValidationError

// TODO: исправить тесты и дописать другие

class SignUpUseCaseTest {
    private lateinit var authRepository: AuthRepository
    private lateinit var validator: EmailPasswordValidator
    private lateinit var useCase: SignUpUseCase
    private val email = "invalid_email"
    private val password = "invalid_password"

    @BeforeEach
    fun setUp() {
        authRepository = mockk()
        validator = mockk()
        useCase = SignUpUseCase(authRepository, validator)
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(EmailPasswordValidator)
    }

    @Test
    fun `signUp with validate failure`() = runTest {
        every { validator.validate(email, password, password) } returns Result.Failure(AuthValidationError.INVALID_EMAIL_FORMAT)

        val expected = listOf(Result.Failure(AuthValidationError.INVALID_EMAIL_FORMAT))
        val actual = useCase(email, password, password).toList()

        assertEquals(expected, actual)
        coVerify { authRepository wasNot Called }
    }

    @Test
    fun `signUp with validate success`() = runTest {
        every { validator.validate(email, password, password) } returns Result.Success(Unit)
        coEvery { authRepository.signUp(email, password) } returns flowOf(Result.InProgress, Result.Success(Unit))

        val expected = listOf(Result.InProgress, Result.Success(Unit))
        val actual = useCase(email, password, password).toList()

        assertEquals(expected, actual)
        coVerify(exactly = 1) { authRepository.signUp(email, password) }
    }
}