package ru.lonelywh1te.introgym.features.onboarding.domain.usecase

import ru.lonelywh1te.introgym.data.prefs.user.Gender
import ru.lonelywh1te.introgym.features.onboarding.domain.repository.OnboardingRepository
import java.time.LocalDate

class SetUserPreferencesUseCase(private val repository: OnboardingRepository) {
    operator fun invoke(name: String, gender: Gender?, birthday: LocalDate) {
        val userName = name.ifBlank { null }
        val userBirthday = if (birthday != LocalDate.now()) birthday else null

        repository.setUserPreferences(userName, gender, userBirthday)
    }
}