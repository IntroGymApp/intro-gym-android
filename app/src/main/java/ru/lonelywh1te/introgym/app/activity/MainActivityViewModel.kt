package ru.lonelywh1te.introgym.app.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.ErrorDispatcher
import ru.lonelywh1te.introgym.data.prefs.LaunchPreferences

class MainActivityViewModel(
    private val launchPreferences: LaunchPreferences,
    private val errorDispatcher: ErrorDispatcher,
): ViewModel() {
    private val _errorMessages: MutableSharedFlow<String> = MutableSharedFlow()
    val errors get() = _errorMessages.asSharedFlow()

    val isFirstLaunch get() = launchPreferences.getIsFirstLaunch()
    val onboardingCompleted get() = launchPreferences.getIsOnboardingCompleted()

    init {
        viewModelScope.launch {
            errorDispatcher.errorMessages.collect {
                _errorMessages.emit(it)
            }
        }
    }
}