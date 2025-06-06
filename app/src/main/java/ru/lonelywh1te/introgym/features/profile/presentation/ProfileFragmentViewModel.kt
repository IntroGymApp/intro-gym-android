package ru.lonelywh1te.introgym.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.toUIState
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.features.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.data.prefs.user.UserInfo
import ru.lonelywh1te.introgym.features.profile.domain.usecase.GetUserInfoUseCase
import ru.lonelywh1te.introgym.features.profile.domain.usecase.SignOutUseCase

class ProfileFragmentViewModel(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val authRepository: AuthRepository,
): ViewModel() {
    private val isSignedIn get() = authRepository.isSignedIn()

    private val _userInfoState: MutableStateFlow<UIState<UserInfo>?> = MutableStateFlow(null)
    val userInfoState get() = _userInfoState.asStateFlow()

    private val dispatcher = Dispatchers.IO

    fun loadUserInfo() {
        if (!isSignedIn) return

        viewModelScope.launch (dispatcher) {
            getUserInfoUseCase().collect { result ->
                _userInfoState.value = result.toUIState()
            }
        }
    }

    fun getIsSignedIn(): Boolean {
        return isSignedIn
    }


    fun signOut() {
        signOutUseCase()
    }
}