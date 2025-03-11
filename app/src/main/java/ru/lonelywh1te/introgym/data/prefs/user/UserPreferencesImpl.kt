package ru.lonelywh1te.introgym.data.prefs.user

import android.content.Context
import ru.lonelywh1te.introgym.data.prefs.UserPreferences
import java.time.LocalDate

class UserPreferencesImpl(context: Context): UserPreferences {
    private val prefs = context.getSharedPreferences(USER_PREFERENCES_KEY, Context.MODE_PRIVATE)

    override var username: String?
        get() = prefs.getString(USERNAME_KEY, null)
        set(value) = prefs.edit().putString(USERNAME_KEY, value).apply()

    override var gender: Gender?
        get() = prefs.getString(GENDER_KEY, null)?.let { Gender.valueOf(it) }
        set(value) = prefs.edit().putString(GENDER_KEY, value?.name).apply()


    override var birthday: LocalDate?
        get() = prefs.getString(BIRTHDAY_KEY, null)?.let { LocalDate.parse(it) }
        set(value) = prefs.edit().putString(BIRTHDAY_KEY, value?.toString()).apply()

    override fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val USER_PREFERENCES_KEY = "user_preferences"
        private const val USERNAME_KEY = "username"
        private const val GENDER_KEY = "gender"
        private const val BIRTHDAY_KEY = "birthday"
    }
}