package ru.lonelywh1te.introgym.features.auth.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.features.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.features.auth.domain.CredentialsValidator
import ru.lonelywh1te.introgym.features.auth.domain.model.OtpType

class SendOtpUseCase(
    private val repository: AuthRepository,
    private val validator: CredentialsValidator,
) {
    operator fun invoke(email: String, otpType: OtpType): Flow<Result<Unit>> {
        validator.validateEmail(email)
            .onFailure { return flowOf(Result.Failure(it)) }

        return repository.sendOtp(email, otpType.name)
    }
}