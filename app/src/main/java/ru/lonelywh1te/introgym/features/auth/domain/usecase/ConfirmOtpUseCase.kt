package ru.lonelywh1te.introgym.features.auth.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.features.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.features.auth.domain.OtpRepository
import ru.lonelywh1te.introgym.features.auth.domain.OtpValidator
import ru.lonelywh1te.introgym.features.auth.domain.model.OtpType

class ConfirmOtpUseCase(
    private val repository: OtpRepository,
    private val otpValidator: OtpValidator,
) {
    operator fun invoke(otp: String, otpType: OtpType): Flow<Result<Unit>> {
        otpValidator.validate(otp)
            .onFailure { flowOf(it) }

        return repository.confirmOtp(otp, otpType.name)
    }
}