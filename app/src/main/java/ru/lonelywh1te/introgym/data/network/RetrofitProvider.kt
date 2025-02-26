package ru.lonelywh1te.introgym.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {
    private const val UNAUTHORIZED_BASE_URL = "https://introgym.ru/uaa/api/v1/"
    private const val AUTHORIZED_BASE_URL = "https://introgym.ru/aa/api/v1/"

    private val httpClient = OkHttpClient.Builder().build()

    fun getUnauthorizedRetrofit(): Retrofit = createRetrofit(UNAUTHORIZED_BASE_URL)
    fun getAuthorizedRetrofit(): Retrofit = createRetrofit(AUTHORIZED_BASE_URL)

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}