package ru.lonelywh1te.introgym.features.auth.domain

import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthValidationError

object EmailPasswordValidator {

    fun validateEmail(email: String): Result<Unit> {
        return if (isValidEmail(email)) Result.Success(Unit) else Result.Failure(AuthValidationError.InvalidEmailFormat())
    }

    fun validatePassword(password: String): List<AuthValidationError> {
        val errors = mutableListOf<AuthValidationError>()

        if (password.length < 8) errors.add(AuthValidationError.PasswordTooShort())
        if (!password.any { it.isUpperCase() }) errors.add(AuthValidationError.PasswordMissingUppercase())
        if (!password.any { !it.isLetterOrDigit() }) errors.add(AuthValidationError.PasswordMissingSpecialSymbol())

        return errors
    }

    fun validateEmailAndPassword(email: String, password: String): Result<Unit> {
        return when {
            !isValidEmail(email) -> Result.Failure(AuthValidationError.InvalidEmailFormat())
            !isValidPassword(password) -> Result.Failure(AuthValidationError.InvalidPasswordFormat())
            else -> Result.Success(Unit)
        }
    }

    fun validateEmailAndPasswordWithConfirm(email: String, password: String, confirmPassword: String): Result<Unit> {
        return when {
            !isValidEmail(email) -> Result.Failure(AuthValidationError.InvalidEmailFormat())
            !isValidPassword(password) -> Result.Failure(AuthValidationError.InvalidPasswordFormat())
            !comparePasswords(password, confirmPassword) -> Result.Failure(AuthValidationError.PasswordMismatch())
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