package ru.lonelywh1te.introgym.features.auth.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.features.auth.domain.model.OtpType

class ConfirmOtpUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(otp: String, otpType: OtpType): Flow<Result<Unit>> {
        return repository.confirmOtp(otp, otpType.name)
    }
}