package ru.lonelywh1te.introgym.features.auth.domain

import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthValidationError

object EmailPasswordValidator {

    fun validate(email: String, password: String? = null, confirmPassword: String? = null): Result<Unit> {
        if (!isValidEmail(email)) return Result.Failure(AuthValidationError.INVALID_EMAIL_FORMAT)
        if (password != null) return validatePassword(password, confirmPassword)

        return Result.Success(Unit)
    }

    private fun validatePassword(password: String, confirmPassword: String? = null): Result<Unit> {
        return when {
            getPasswordStates(password).isNotEmpty() -> Result.Failure(AuthValidationError.INVALID_PASSWORD_FORMAT)
            confirmPassword != null && password != confirmPassword -> Result.Failure(AuthValidationError.PASSWORD_MISMATCH)
            else -> Result.Success(Unit)
        }
    }

    fun getPasswordStates(password: String): List<AuthValidationError> {
        val errors = mutableListOf<AuthValidationError>()

        if (password.length < 8) errors.add(AuthValidationError.PASSWORD_TOO_SHORT)
        if (!password.any { it.isUpperCase() }) errors.add(AuthValidationError.PASSWORD_MISSING_UPPERCASE)
        if (!password.any { !it.isLetterOrDigit() }) errors.add(AuthValidationError.PASSWORD_MISSING_SPECIAL_SYMBOL)

        return errors
    }

    private fun isValidEmail(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
    }
}