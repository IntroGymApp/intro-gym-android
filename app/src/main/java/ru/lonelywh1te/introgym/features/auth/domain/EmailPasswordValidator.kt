package ru.lonelywh1te.introgym.features.auth.domain

import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthValidationError

object EmailPasswordValidator {

    fun validateEmail(email: String): Result<Unit> {
        return if (isValidEmail(email)) Result.Success(Unit) else Result.Failure(AuthValidationError.INVALID_EMAIL_FORMAT)
    }

    fun validatePassword(password: String): List<AuthValidationError> {
        val errors = mutableListOf<AuthValidationError>()

        if (password.length < 8) errors.add(AuthValidationError.PASSWORD_TOO_SHORT)
        if (!password.any { it.isUpperCase() }) errors.add(AuthValidationError.PASSWORD_MISSING_UPPERCASE)
        if (!password.any { !it.isLetterOrDigit() }) errors.add(AuthValidationError.PASSWORD_MISSING_SPECIAL_SYMBOL)

        return errors
    }

    fun validateEmailAndPassword(email: String, password: String): Result<Unit> {
        return when {
            !isValidEmail(email) -> Result.Failure(AuthValidationError.INVALID_EMAIL_FORMAT)
            !isValidPassword(password) -> Result.Failure(AuthValidationError.INVALID_PASSWORD_FORMAT)
            else -> Result.Success(Unit)
        }
    }

    fun validateEmailAndPasswordWithConfirm(email: String, password: String, confirmPassword: String): Result<Unit> {
        return when {
            !isValidEmail(email) -> Result.Failure(AuthValidationError.INVALID_EMAIL_FORMAT)
            !isValidPassword(password) -> Result.Failure(AuthValidationError.INVALID_PASSWORD_FORMAT)
            !comparePasswords(password, confirmPassword) -> Result.Failure(AuthValidationError.PASSWORD_MISMATCH)
            else -> Result.Success(Unit)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
    }

    private fun isValidPassword(password: String): Boolean {
        return validatePassword(password).isEmpty()
    }

    private fun comparePasswords(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }
}