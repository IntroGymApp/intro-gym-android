package ru.lonelywh1te.introgym.features.profile.presentation

import androidx.lifecycle.ViewModel
import ru.lonelywh1te.introgym.data.prefs.UserPreferences
import ru.lonelywh1te.introgym.features.auth.domain.AuthRepository
import java.time.LocalDate

class ProfileFragmentViewModel(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences,
): ViewModel() {
    private val username: String? get() = userPreferences.username
    private val isSignedIn: Boolean get() = authRepository.isSignedIn()
    private val registerDate: LocalDate = LocalDate.now()
    private val countOfWorkouts: Int = 0

    fun getUserName(): String {
        return username?: "Пользователь"
    }

    fun getRegisterDate(): LocalDate {
        return registerDate
    }

    fun getCountOfWorkouts(): Int {
        return countOfWorkouts
    }

    fun getIsSignedIn(): Boolean {
        return isSignedIn
    }

}