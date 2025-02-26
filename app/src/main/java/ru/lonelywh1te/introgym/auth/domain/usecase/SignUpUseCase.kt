package ru.lonelywh1te.introgym.auth.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.lonelywh1te.introgym.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.auth.domain.validator.EmailPasswordValidator
import ru.lonelywh1te.introgym.core.result.Result

class SignUpUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, confirmPassword: String): Flow<Result<Unit>> {
        val validateResult = EmailPasswordValidator.validate(email, password, confirmPassword)
        if (validateResult !is Result.Success) return flowOf(validateResult)

        return repository.signUp(email, password)
    }
}