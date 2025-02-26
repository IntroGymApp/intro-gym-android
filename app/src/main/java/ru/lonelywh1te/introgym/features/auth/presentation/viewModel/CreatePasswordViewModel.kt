package ru.lonelywh1te.introgym.features.auth.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.features.auth.domain.usecase.SignUpUseCase
import ru.lonelywh1te.introgym.core.result.toUIState
import ru.lonelywh1te.introgym.core.ui.UIState

class CreatePasswordViewModel(
    private val signUpUseCase: SignUpUseCase,
): ViewModel() {
    private val _signUpResult = MutableSharedFlow<UIState<*>>()
    val signUpResult get() = _signUpResult.asSharedFlow()

    private val dispatcher = Dispatchers.IO

    fun signUp(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch(dispatcher) {
            signUpUseCase(email, password, confirmPassword).collect { result ->
                _signUpResult.emit(result.toUIState())
            }
        }
    }
}