package ru.lonelywh1te.introgym.features.auth.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.features.auth.domain.EmailPasswordValidator
import ru.lonelywh1te.introgym.features.auth.domain.model.OtpType

class SendOtpUseCase(
    private val repository: AuthRepository,
    private val validator: EmailPasswordValidator,
) {
    suspend operator fun invoke(email: String, otpType: OtpType): Flow<Result<Unit>> {
        val validateResult = validator.validate(email)
        if (validateResult !is Result.Success) return flowOf(validateResult)

        return repository.sendOtp(email, otpType.name)
    }
}