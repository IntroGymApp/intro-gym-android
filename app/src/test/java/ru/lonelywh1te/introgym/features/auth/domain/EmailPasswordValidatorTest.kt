package ru.lonelywh1te.introgym.features.auth.domain

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ru.lonelywh1te.introgym.features.auth.domain.error.ValidationError
import ru.lonelywh1te.introgym.core.result.Result

class EmailPasswordValidatorTest {
    private val validator = EmailPasswordValidator

    @Nested
    inner class ValidateEmailWithConfirmPasswordTests() {

        @Test
        fun `validate with correct email and similar password returns Success`() {
            val email = "test@example.com"
            val password = "TestPassword123!"

            val expected = Result.Success(Unit)
            val actual = validator.validate(email, password, password)

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `validate with correct email and different password returns PASSWORD_MISMATCH`() {
            val email = "test@example.com"
            val password = "TestPassword123!"
            val confirmPassword = "Testpassword123"

            val expected = Result.Failure(ValidationError.PASSWORD_MISMATCH)
            val actual = validator.validate(email, password, confirmPassword)

            Assertions.assertEquals(expected, actual)
        }
    }

    @Nested
    inner class ValidateEmailAndPasswordTests() {
        @Test
        fun `validate with correct email and password returns Success`() {
            val email = "test@example.com"
            val password = "TestPassword123!"

            val expected = Result.Success(Unit)
            val actual = validator.validate(email, password)

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `validate with incorrect email and password returns INVALID_EMAIL_FORMAT`() {
            val email = "tes t@example_com"
            val password = "reff fds"

            val expected = Result.Failure(ValidationError.INVALID_EMAIL_FORMAT)
            val actual = validator.validate(email, password)

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `validate with incorrect email and correct password returns INVALID_EMAIL_FORMAT`() {
            val email = "tes t@example_com"
            val password = "TestPassword123!"

            val expected = Result.Failure(ValidationError.INVALID_EMAIL_FORMAT)
            val actual = validator.validate(email, password)

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `validate with correct email and incorrect password returns INVALID_PASSWORD_FORMAT`() {
            val email = "test@example.com"
            val password = "reff fds"

            val expected = Result.Failure(ValidationError.INVALID_PASSWORD_FORMAT)
            val actual = validator.validate(email, password)

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `validate with correct email and empty password returns INVALID_PASSWORD_FORMAT`() {
            val email = "test@example.com"
            val password = ""

            val expected = Result.Failure(ValidationError.INVALID_PASSWORD_FORMAT)
            val actual = validator.validate(email, password)

            Assertions.assertEquals(expected, actual)
        }
    }

    @Nested
    inner class ValidateEmailTests() {
        @Test
        fun `validate with correct email returns Success`() {
            val email = "test@example.com"

            val expected = Result.Success(Unit)
            val actual = validator.validate(email)

            Assertions.assertEquals(expected, actual)
        }

        @Test
        fun `validate with incorrect email returns INVALID_EMAIL_FORMAT`() {
            val email = "tes t@example_com"

            val expected = Result.Failure(ValidationError.INVALID_EMAIL_FORMAT)
            val actual = validator.validate(email)

            Assertions.assertEquals(expected, actual)
        }
    }

}