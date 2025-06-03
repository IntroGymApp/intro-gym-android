package ru.lonelywh1te.introgym.features.auth.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.features.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.features.auth.domain.CredentialsValidator
import ru.lonelywh1te.introgym.features.auth.domain.model.UserCredentials

class SignInUseCase(
    private val repository: AuthRepository,
    private val validator: CredentialsValidator,
) {
    operator fun invoke(userCredentials: UserCredentials): Flow<Result<Unit>> {
        validator.validateUserCredentials(userCredentials)
            .onFailure { return flowOf(Result.Failure(it)) }

        return repository.signIn(userCredentials)
    }
}