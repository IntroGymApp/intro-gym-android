package ru.lonelywh1te.introgym.auth.data.storage

interface AuthStorage {
    fun saveSessionId(sessionId: String)
    fun getSessionId(): String?
    fun clearSessionId()
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun saveTokens(accessToken: String, refreshToken: String)
    fun clearTokens()
    fun clearAll()
}