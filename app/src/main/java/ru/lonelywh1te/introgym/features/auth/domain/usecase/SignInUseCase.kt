package ru.lonelywh1te.introgym.features.auth.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.features.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.features.auth.domain.EmailPasswordValidator

class SignInUseCase(
    private val repository: AuthRepository,
    private val validator: EmailPasswordValidator,
) {
    suspend operator fun invoke(email: String, password: String): Flow<Result<Unit>> {
        validator.validateEmailAndPassword(email, password)
            .onFailure { return flowOf(Result.Failure(it)) }

        return repository.signIn(email, password)
    }
}