package ru.lonelywh1te.introgym.data.prefs.user

import android.content.Context
import android.util.Log
import ru.lonelywh1te.introgym.data.prefs.UserPreferences
import java.time.LocalDate

class UserPreferencesImpl(context: Context): UserPreferences {
    private val prefs = context.getSharedPreferences(USER_PREFERENCES_KEY, Context.MODE_PRIVATE)

    override fun getUserInfo(): UserInfo? {
        val username = prefs.getString(USERNAME_KEY, null) ?: return null
        val registerDate = prefs.getString(REGISTER_DATE_KEY, null) ?: return null

        return UserInfo(
            name = username,
            registerDate = LocalDate.parse(registerDate)
        ).also {
            Log.d("UserPreferencesImpl", "Get UserInfo: $it")
        }
    }

    override fun saveUserInfo(userInfo: UserInfo) {
        prefs.edit()
            .putString(USERNAME_KEY, userInfo.name)
            .putString(REGISTER_DATE_KEY, userInfo.registerDate.toString())
            .apply()

        Log.d("UserPreferencesImpl", "UserInfo saved!: $userInfo")
    }

    override fun clear() {
        prefs.edit().clear().apply()
        Log.d("UserPreferencesImpl", "UserPreferences cleared")
    }

    companion object {
        private const val USER_PREFERENCES_KEY = "user_preferences"
        private const val USERNAME_KEY = "username"
        private const val REGISTER_DATE_KEY = "register_date"
    }
}