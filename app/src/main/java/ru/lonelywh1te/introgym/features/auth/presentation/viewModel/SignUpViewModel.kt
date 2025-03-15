package ru.lonelywh1te.introgym.features.auth.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.core.result.toUIState
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.features.auth.domain.EmailPasswordValidator
import ru.lonelywh1te.introgym.features.auth.domain.error.ValidationError
import ru.lonelywh1te.introgym.features.auth.domain.usecase.SignUpUseCase

class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase,
    private val validator: EmailPasswordValidator,
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

    fun validate(email: String, password: String, confirmPassword: String): Result<Unit> {
        return validator.validate(email, password, confirmPassword)
    }

    fun getPasswordState(password: String): List<ValidationError> {
        return validator.getPasswordStates(password)
    }
}