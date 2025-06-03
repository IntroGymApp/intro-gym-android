package ru.lonelywh1te.introgym.data.prefs

import ru.lonelywh1te.introgym.data.prefs.user.Gender
import java.time.LocalDate

interface UserPreferences {
    var username: String?
    var gender: Gender?
    var birthday: LocalDate?
    var registerDate: LocalDate?

    fun clearAll()
}