package ru.lonelywh1te.introgym.data.prefs

import java.time.LocalDate

interface UserPreferences {
    var username: String?
    var registerDate: LocalDate?

    fun clearAll()
}