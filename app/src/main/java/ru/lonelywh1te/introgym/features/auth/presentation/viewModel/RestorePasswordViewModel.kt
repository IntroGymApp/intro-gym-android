package ru.lonelywh1te.introgym.features.auth.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.toUIState
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.features.auth.domain.EmailPasswordValidator
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthValidationError
import ru.lonelywh1te.introgym.features.auth.domain.usecase.ChangePasswordUseCase

class RestorePasswordViewModel(
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val validator: EmailPasswordValidator,
): ViewModel() {
    private val _changePasswordResult: MutableSharedFlow<UIState<*>> = MutableSharedFlow(replay = 1)
    val changePasswordResult get() = _changePasswordResult

    private val dispatcher = Dispatchers.IO

    fun changePassword(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch(dispatcher) {
            changePasswordUseCase(email, password, confirmPassword).collect { result ->
                _changePasswordResult.emit(result.toUIState())
            }
        }
    }

    fun getPasswordState(password: String): List<AuthValidationError> {
        return validator.getPasswordStates(password)
    }
}