package ru.lonelywh1te.introgym.features.onboarding.presentation.viewModel

import androidx.lifecycle.ViewModel
import ru.lonelywh1te.introgym.data.prefs.user.Gender
import ru.lonelywh1te.introgym.features.onboarding.domain.usecase.SetUserPreferencesUseCase
import java.time.LocalDate

class AboutUserViewModel(
    private val setUserPreferencesUseCase: SetUserPreferencesUseCase,
): ViewModel() {

    fun saveUserPreferences(name: String, gender: Gender?, birthday: LocalDate) {
        setUserPreferencesUseCase(name, gender, birthday)
    }

}