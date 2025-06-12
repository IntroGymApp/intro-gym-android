package ru.lonelywh1te.introgym.features.settings.presentation

import androidx.lifecycle.ViewModel
import ru.lonelywh1te.introgym.data.prefs.SettingsPreferences

class SettingsFragmentViewModel(
    private val settingsPreferences: SettingsPreferences,
):ViewModel() {
    val isNotificationsEnabled get() = settingsPreferences.getIsNotificationEnabled()
    val isDarkThemeEnabled get() = settingsPreferences.getIsDarkTheme()

    fun setIsNotificationsEnabled(state: Boolean) {
        settingsPreferences.setIsNotificationEnabled(state)
    }

    fun setIsDarkTheme(state: Boolean) {
        settingsPreferences.setIsDarkTheme(state)
    }
}