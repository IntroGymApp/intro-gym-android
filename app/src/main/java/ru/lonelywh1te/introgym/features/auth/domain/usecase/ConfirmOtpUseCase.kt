package ru.lonelywh1te.introgym.features.auth.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.features.auth.domain.OtpValidator
import ru.lonelywh1te.introgym.features.auth.domain.model.OtpType
import javax.xml.validation.Validator

class ConfirmOtpUseCase(
    private val repository: AuthRepository,
    private val validator: OtpValidator,
) {
    suspend operator fun invoke(otp: String, otpType: OtpType): Flow<Result<Unit>> {
        val validateResult = validator.validate(otp)
        if (validateResult !is Result.Success) return flowOf(validateResult)

        return repository.confirmOtp(otp, otpType.name)
    }
}