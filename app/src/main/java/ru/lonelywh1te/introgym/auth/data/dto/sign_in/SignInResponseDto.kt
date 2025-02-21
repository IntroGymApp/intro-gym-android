package ru.lonelywh1te.introgym.auth.data.dto.sign_in

import com.google.gson.annotations.SerializedName

data class SignInResponseDto(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
)
