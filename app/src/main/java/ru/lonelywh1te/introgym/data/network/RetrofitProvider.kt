package ru.lonelywh1te.introgym.data.network

import android.util.Log
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.lonelywh1te.introgym.features.auth.data.token.AuthInterceptor
import java.util.concurrent.TimeUnit

object RetrofitProvider {
    private const val UNAUTHORIZED_BASE_URL = "https://introgym.ru/uaa/api/v1/"
    private const val AUTHORIZED_BASE_URL = "https://introgym.ru/aa/api/v1/"

    private val okhttpClient = OkHttpClient
        .Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)

    fun getUnauthorizedRetrofit(): Retrofit = createRetrofit(UNAUTHORIZED_BASE_URL)
    fun getAuthorizedRetrofit(
        authenticator: Authenticator,
        authInterceptor: AuthInterceptor,
    ): Retrofit = createRetrofit(AUTHORIZED_BASE_URL, authenticator, authInterceptor)

    private fun createRetrofit(
        baseUrl: String,
        authenticator: Authenticator? = null,
        authInterceptor: AuthInterceptor? = null
    ): Retrofit {
        if (authenticator != null && authInterceptor != null) {
            okhttpClient
                .authenticator(authenticator)
                .addInterceptor(authInterceptor)
        }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okhttpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}