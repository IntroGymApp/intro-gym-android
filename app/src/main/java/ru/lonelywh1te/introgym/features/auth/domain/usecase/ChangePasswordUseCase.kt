package ru.lonelywh1te.introgym.features.auth.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.features.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.features.auth.domain.CredentialsValidator
import ru.lonelywh1te.introgym.features.auth.domain.model.UserCredentials

class ChangePasswordUseCase(
    private val repository: AuthRepository,
    private val validator: CredentialsValidator,
) {
    operator fun invoke(userCredentials: UserCredentials, confirmPassword: String): Flow<Result<Unit>> {
        validator.validateUserCredentialsWithConfirmPassword(userCredentials, confirmPassword)
            .onFailure { flowOf(it) }

        return repository.changePassword(userCredentials)
    }
}