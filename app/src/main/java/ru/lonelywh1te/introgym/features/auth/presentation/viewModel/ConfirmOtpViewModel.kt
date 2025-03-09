package ru.lonelywh1te.introgym.features.auth.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.toUIState
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.data.network.NetworkError
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthError
import ru.lonelywh1te.introgym.features.auth.domain.model.OtpType
import ru.lonelywh1te.introgym.features.auth.domain.usecase.ConfirmOtpUseCase
import ru.lonelywh1te.introgym.features.auth.domain.usecase.SendOtpUseCase

class ConfirmOtpViewModel(
    private val sendOtpUseCase: SendOtpUseCase,
    private val confirmOtpUseCase: ConfirmOtpUseCase,
): ViewModel() {
    private val _sendOtpResult = MutableSharedFlow<UIState<*>>(replay = 1)
    val sendOtpResult get() = _sendOtpResult.asSharedFlow()

    private val _confirmOtpResult = MutableSharedFlow<UIState<*>>()
    val confirmOtpResult get() = _confirmOtpResult.asSharedFlow()

    private var otpType: OtpType? = null
    private val dispatcher = Dispatchers.IO

    fun sendOtp(email: String) {
        Log.d("ConfirmOtpViewModel", "$email, $otpType")
        viewModelScope.launch(dispatcher) {
            if (otpType == null) {
                _confirmOtpResult.emit(UIState.Failure(NetworkError.UNKNOWN))
                return@launch
            }

            sendOtpUseCase(email, otpType!!).collect { result ->
                Log.d("ConfirmOtpViewModel", "$result")
                _sendOtpResult.emit(result.toUIState())
            }
        }
    }

    fun confirmOtp(otp: String) {
        viewModelScope.launch(dispatcher) {
            if (otpType == null) {
                _confirmOtpResult.emit(UIState.Failure(NetworkError.UNKNOWN))
                return@launch
            }

            confirmOtpUseCase(otp, otpType!!).collect { result ->
                _confirmOtpResult.emit(result.toUIState())
            }
        }
    }

    fun setOtpType(otpType: OtpType) {
        this.otpType = otpType
    }

}