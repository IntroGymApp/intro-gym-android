package ru.lonelywh1te.introgym.features.auth.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.toUIState
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.features.auth.domain.model.UserCredentials
import ru.lonelywh1te.introgym.features.auth.domain.usecase.SignInUseCase

class SignInViewModel(
    private val signInUseCase: SignInUseCase,
): ViewModel() {
    private val _signInResult = MutableSharedFlow<UIState<*>>()
    val signInResult get() = _signInResult.asSharedFlow()

    private val dispatcher = Dispatchers.IO

    fun signIn(email: String, password: String) {
        viewModelScope.launch(dispatcher) {
            val credentials = UserCredentials(email, password)
            signInUseCase(credentials).collect { result ->
                _signInResult.emit(result.toUIState())
            }
        }
    }
}