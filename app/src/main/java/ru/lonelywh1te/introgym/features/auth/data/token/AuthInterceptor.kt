package ru.lonelywh1te.introgym.features.auth.data.token

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import ru.lonelywh1te.introgym.features.auth.data.storage.AuthStorage

class AuthInterceptor(
    private val authStorage: AuthStorage
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = authStorage.getAccessToken() ?: return chain.proceed(chain.request())

        val newRequest = chain.request()
            .newBuilder()
            .removeHeader("Authorization")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        Log.d("AuthInterceptor", "New request: $newRequest")

        return chain.proceed(newRequest)
    }

}