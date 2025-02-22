package ru.lonelywh1te.introgym.auth.data.prefs

interface AuthStorage {
    fun saveSessionId(sessionId: String)
    fun getSessionId(): String?
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun saveTokens(accessToken: String, refreshToken: String)
    fun clear()
}