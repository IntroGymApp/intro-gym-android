package ru.lonelywh1te.introgym.features.auth.data.token

import android.util.Log
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.lonelywh1te.introgym.core.result.getOrNull
import ru.lonelywh1te.introgym.features.auth.data.storage.AuthStorage
import ru.lonelywh1te.introgym.features.auth.domain.AuthRepository

class TokenAuthenticator(
    private val authStorage: AuthStorage,
    private val authRepository: AuthRepository,
): Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        val newTokenPair = runBlocking {
            authRepository.refreshToken()
        }.getOrNull() ?: return null

        authStorage.saveTokens(newTokenPair.accessToken, newTokenPair.refreshToken)

        Log.d("TokenAuthenticator", "New token pair: $newTokenPair")

        return response.request
            .newBuilder()
            .header("Authorization", "Bearer ${newTokenPair.accessToken}")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}