package ru.lonelywh1te.introgym.auth.domain.validator

import ru.lonelywh1te.introgym.auth.domain.error.EmailPasswordError
import ru.lonelywh1te.introgym.core.result.Result

object EmailPasswordValidator {
    fun validate(email: String, password: String): Result<Unit> {
        return when {
            !isValidEmail(email) -> Result.Failure(EmailPasswordError.INVALID_EMAIL_FORMAT)
            !isValidPassword(password) -> Result.Failure(EmailPasswordError.INVALID_PASSWORD_FORMAT)
            else -> Result.Success(Unit)
        }

    }

    fun validate(email: String): Result<Unit> {
        return when {
            !isValidEmail(email) -> Result.Failure(EmailPasswordError.INVALID_EMAIL_FORMAT)
            else -> Result.Success(Unit)
        }

    }

    private fun isValidEmail(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isDigit() } &&
                password.any { it.isLetter() } &&
                password.any { !it.isLetterOrDigit() }
    }
}