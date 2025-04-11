package ru.lonelywh1te.introgym.features.auth.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.features.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.features.auth.domain.EmailPasswordValidator

class ChangePasswordUseCase(
    private val repository: AuthRepository,
    private val validator: EmailPasswordValidator,
) {
    suspend operator fun invoke(email: String, password: String, confirmPassword: String): Flow<Result<Unit>> {
        validator.validateEmailAndPasswordWithConfirm(email, password, confirmPassword)
            .onFailure { flowOf(it) }

        return repository.changePassword(email, password)
    }
}