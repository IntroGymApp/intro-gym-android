package ru.lonelywh1te.introgym.features.auth.domain

import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.auth.domain.error.ValidationError

object EmailPasswordValidator {

    fun validate(email: String, password: String, confirmPassword: String): Result<Unit> {
        return when {
            !isValidEmail(email) -> Result.Failure(ValidationError.INVALID_EMAIL_FORMAT)
            !isValidPassword(password) -> Result.Failure(ValidationError.INVALID_PASSWORD_FORMAT)
            !matchPassword(password, confirmPassword) -> Result.Failure(ValidationError.PASSWORD_MISMATCH)
            else -> Result.Success(Unit)
        }
    }

    fun validate(email: String, password: String): Result<Unit> {
        return when {
            !isValidEmail(email) -> Result.Failure(ValidationError.INVALID_EMAIL_FORMAT)
            !isValidPassword(password) -> Result.Failure(ValidationError.INVALID_PASSWORD_FORMAT)
            else -> Result.Success(Unit)
        }
    }

    fun validate(email: String): Result<Unit> {
        return when {
            !isValidEmail(email) -> Result.Failure(ValidationError.INVALID_EMAIL_FORMAT)
            else -> Result.Success(Unit)
        }
    }

    fun validatePassword(password: String): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()

        if (password.length <= 8) errors.add(ValidationError.PASSWORD_TOO_SHORT)
        if (!password.any { it.isUpperCase() }) errors.add(ValidationError.PASSWORD_MISSING_UPPERCASE)
        if (!password.any { !it.isLetterOrDigit() }) errors.add(ValidationError.PASSWORD_MISSING_SPECIAL_SYMBOL)

        return errors
    }


    private fun matchPassword(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    private fun isValidEmail(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
    }

    private fun isValidPassword(password: String): Boolean {
        return validatePassword(password).isEmpty()
    }
}