package ru.lonelywh1te.introgym.features.profile.domain.usecase

import ru.lonelywh1te.introgym.data.prefs.UserPreferences
import ru.lonelywh1te.introgym.features.auth.data.storage.AuthStorage

class SignOutUseCase(
    private val authStorage: AuthStorage,
    private val userPreferences: UserPreferences,
) {
    operator fun invoke() {
        authStorage.clearTokens()
        userPreferences.clearAll()
    }
}