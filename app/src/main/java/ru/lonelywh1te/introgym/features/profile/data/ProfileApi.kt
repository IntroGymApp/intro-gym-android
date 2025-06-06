package ru.lonelywh1te.introgym.features.profile.data

import retrofit2.Response
import retrofit2.http.GET
import ru.lonelywh1te.introgym.features.profile.data.dto.UserProfileDataDto


interface ProfileApi {

    @GET("user/profile")
    suspend fun getUserInfo(): Response<UserProfileDataDto>

}