package ru.lonelywh1te.introgym.features.auth.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.features.auth.domain.EmailPasswordValidator

class SignInUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Flow<Result<Unit>> {
        val validateResult = EmailPasswordValidator.validate(email, password)
        if (validateResult !is Result.Success) return flowOf(validateResult)

        return repository.signIn(email, password)
    }
}