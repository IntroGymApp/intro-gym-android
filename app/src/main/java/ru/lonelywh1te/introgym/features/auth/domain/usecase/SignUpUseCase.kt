package ru.lonelywh1te.introgym.features.auth.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.features.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.features.auth.domain.CredentialsValidator
import ru.lonelywh1te.introgym.features.auth.domain.model.SignUpCredentials

class SignUpUseCase(
    private val repository: AuthRepository,
    private val validator: CredentialsValidator,
) {
    operator fun invoke(signUpData: SignUpCredentials, confirmPassword: String): Flow<Result<Unit>> {
        validator.validateSignUpCredentialsWithConfirmPassword(signUpData, confirmPassword)
            .onFailure { return flowOf(Result.Failure(it)) }

        return repository.signUp(signUpData)
    }
}