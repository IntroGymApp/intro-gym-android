package ru.lonelywh1te.introgym.features.auth.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.features.auth.domain.model.OtpType
import ru.lonelywh1te.introgym.features.auth.domain.usecase.ConfirmOtpUseCase
import ru.lonelywh1te.introgym.core.result.toUIState
import ru.lonelywh1te.introgym.core.ui.UIState

class ConfirmOtpViewModel(
    private val confirmOtpUseCase: ConfirmOtpUseCase,
): ViewModel() {
    private val _confirmOtpResult = MutableSharedFlow<UIState<*>>()
    val confirmOtpResult get() = _confirmOtpResult.asSharedFlow()

    private val otpType = OtpType.CONFIRM_SIGNUP
    private val dispatcher = Dispatchers.IO

    fun confirmOtp(otp: String) {
        viewModelScope.launch(dispatcher) {
            confirmOtpUseCase(otp, otpType).collect { result ->
                _confirmOtpResult.emit(result.toUIState())
            }
        }
    }
}