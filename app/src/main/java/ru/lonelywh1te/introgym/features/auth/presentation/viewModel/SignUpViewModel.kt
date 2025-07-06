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
import ru.lonelywh1te.introgym.features.auth.domain.CredentialsValidator
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthValidationError
import ru.lonelywh1te.introgym.features.auth.domain.model.SignUpCredentials
import ru.lonelywh1te.introgym.features.auth.domain.usecase.SignUpUseCase

class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase,
    private val validator: CredentialsValidator,
): ViewModel() {
    private val _signUpResult = MutableSharedFlow<UIState<*>>()
    val signUpResult get() = _signUpResult.asSharedFlow()

    private val dispatcher = Dispatchers.IO

    fun signUp(signUpCredentials: SignUpCredentials, confirmPassword: String) {
        viewModelScope.launch(dispatcher) {
            signUpUseCase(signUpCredentials, confirmPassword).collect { result ->
                _signUpResult.emit(result.toUIState())
            }
        }
    }

    fun validate(signUpCredentials: SignUpCredentials, confirmPassword: String): Result<Unit> {
        return validator.validateSignUpCredentialsWithConfirmPassword(signUpCredentials, confirmPassword)
    }

    fun validatePassword(password: String): List<AuthValidationError> {
        return validator.validatePassword(password)
    }
}