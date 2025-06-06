package ru.lonelywh1te.introgym.data.prefs.user

import java.time.LocalDate

data class UserInfo (
    val name: String,
    val registerDate: LocalDate,
)