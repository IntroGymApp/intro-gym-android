package ru.lonelywh1te.introgym.features.profile.domain

import java.time.LocalDate

data class UserInfo (
    val name: String?,
    val registerDate: LocalDate?,
    val countOfWorkouts: Int,
)