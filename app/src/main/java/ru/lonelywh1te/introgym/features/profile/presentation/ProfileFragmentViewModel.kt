package ru.lonelywh1te.introgym.features.profile.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.features.auth.domain.AuthRepository
import ru.lonelywh1te.introgym.features.profile.domain.UserInfo
import ru.lonelywh1te.introgym.features.profile.domain.usecase.GetUserInfoUseCase

class ProfileFragmentViewModel(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val authRepository: AuthRepository,
): ViewModel() {
    private val isSignedIn get() = authRepository.isSignedIn()

    private val _userInfo: MutableStateFlow<UserInfo?> = MutableStateFlow(null)
    val userInfo get() = _userInfo.asStateFlow()

    private val dispatcher = Dispatchers.IO

    fun loadUserInfo() {
        viewModelScope.launch (dispatcher) {
            _userInfo.value = getUserInfoUseCase()
        }
    }

    fun getIsSignedIn(): Boolean {
        return isSignedIn
    }
}