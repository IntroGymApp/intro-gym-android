package ru.lonelywh1te.introgym.features.auth.data.storage

import android.content.Context
import android.content.SharedPreferences

class AuthSharedPreferencesImpl(private val context: Context): AuthStorage {
    private val prefs: SharedPreferences = context.getSharedPreferences(AUTH_PREFS_NAME, Context.MODE_PRIVATE)

    override fun saveSessionId(sessionId: String) {
        prefs.edit().putString(SESSION_ID_KEY, sessionId).apply()
    }

    override fun getSessionId(): String? {
        return prefs.getString(SESSION_ID_KEY, "")
    }

    override fun clearSessionId() {
        prefs.edit().putString(SESSION_ID_KEY, null).apply()
    }

    override fun getAccessToken(): String? {
        return prefs.getString(ACCESS_TOKEN_KEY, "")
    }

    override fun getRefreshToken(): String? {
        return prefs.getString(REFRESH_TOKEN_KEY, "")
    }

    override fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit()
            .putString(ACCESS_TOKEN_KEY, accessToken)
            .putString(REFRESH_TOKEN_KEY, refreshToken)
            .apply()
    }

    override fun clearTokens() {
        prefs.edit()
            .putString(ACCESS_TOKEN_KEY, null)
            .putString(REFRESH_TOKEN_KEY, null)
            .apply()
    }

    override fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val AUTH_PREFS_NAME = "auth_prefs"

        private const val SESSION_ID_KEY = "session_id"
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
    }
}