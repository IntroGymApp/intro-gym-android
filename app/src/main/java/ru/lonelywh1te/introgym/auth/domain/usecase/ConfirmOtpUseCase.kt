package ru.lonelywh1te.introgym.auth.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.auth.domain.model.OtpType
import ru.lonelywh1te.introgym.core.result.Result

class ConfirmOtpUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(otp: String, otpType: OtpType): Flow<Result<Unit>> {
        return repository.confirmOtp(otp, otpType.name)
    }
}