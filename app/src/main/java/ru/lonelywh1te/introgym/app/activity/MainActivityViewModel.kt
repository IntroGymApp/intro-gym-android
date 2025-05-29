package ru.lonelywh1te.introgym.app.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.BaseError
import ru.lonelywh1te.introgym.core.result.ErrorDispatcher
import ru.lonelywh1te.introgym.data.prefs.SettingsPreferences

class MainActivityViewModel(
    private val settingsPreferences: SettingsPreferences,
    private val errorDispatcher: ErrorDispatcher,
): ViewModel() {
    private val _errors: MutableSharedFlow<BaseError> = MutableSharedFlow()
    val errors get() = _errors.asSharedFlow()

    val isFirstLaunch get() = settingsPreferences.isFirstLaunch
    val onboardingCompleted get() = settingsPreferences.onboardingCompleted

    init {
        viewModelScope.launch {
            errorDispatcher.errors.collect {
                _errors.emit(it)
            }
        }
    }
}