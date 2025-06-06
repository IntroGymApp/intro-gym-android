package ru.lonelywh1te.introgym.data.prefs.user

import android.content.Context
import ru.lonelywh1te.introgym.data.prefs.UserPreferences
import java.time.LocalDate

class UserPreferencesImpl(context: Context): UserPreferences {
    private val prefs = context.getSharedPreferences(USER_PREFERENCES_KEY, Context.MODE_PRIVATE)

    override var username: String?
        get() = prefs.getString(USERNAME_KEY, null)
        set(value) = prefs.edit().putString(USERNAME_KEY, value).apply()

    override var registerDate: LocalDate?
        get() = prefs.getString(REGISTER_DATE_KEY, null)?.let { LocalDate.parse(it) }
        set(value) = prefs.edit().putString(REGISTER_DATE_KEY, value?.toString()).apply()

    override fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val USER_PREFERENCES_KEY = "user_preferences"
        private const val USERNAME_KEY = "username"
        private const val REGISTER_DATE_KEY = "register_date"
    }
}