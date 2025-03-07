package ru.lonelywh1te.introgym.features.auth.domain.usecase

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.features.auth.domain.EmailPasswordValidator
import ru.lonelywh1te.introgym.features.auth.domain.model.OtpType

class SendOtpUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, confirmPassword: String, otpType: OtpType): Flow<Result<Unit>> {
        val validateResult = EmailPasswordValidator.validate(email, password, confirmPassword)
        if (validateResult !is Result.Success) return flowOf(validateResult)

        return repository.sendOtp(email, otpType.name)
    }
}