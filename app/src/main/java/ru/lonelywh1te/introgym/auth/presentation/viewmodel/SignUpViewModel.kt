package ru.lonelywh1te.introgym.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.auth.domain.model.OtpType
import ru.lonelywh1te.introgym.auth.domain.usecase.SendOtpUseCase
import ru.lonelywh1te.introgym.core.result.toUIState
import ru.lonelywh1te.introgym.core.ui.UIState

class SignUpViewModel(
    private val sendOtpUseCase: SendOtpUseCase,
): ViewModel() {
    private val _signUpResult = MutableSharedFlow<UIState<*>>()
    val signUpResult get() = _signUpResult.asSharedFlow()

    private val otpType = OtpType.CONFIRM_SIGNUP
    private val dispatcher = Dispatchers.IO

    fun sendOtp(email: String) {
        viewModelScope.launch(dispatcher) {
            sendOtpUseCase(email, otpType).collect { result ->
                _signUpResult.emit(result.toUIState())
            }
        }
    }
}