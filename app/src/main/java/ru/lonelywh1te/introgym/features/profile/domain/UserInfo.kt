package ru.lonelywh1te.introgym.features.profile.domain

import ru.lonelywh1te.introgym.data.prefs.user.Gender
import java.time.LocalDate

data class UserInfo (
    val name: String?,
    val gender: Gender?,
    val registerDate: LocalDate?,
    val birthDate: LocalDate?,
    val countOfWorkouts: Int,
)