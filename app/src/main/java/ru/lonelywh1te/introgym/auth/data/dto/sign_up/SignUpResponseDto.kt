package ru.lonelywh1te.introgym.auth.data.dto.sign_up

import com.google.gson.annotations.SerializedName

data class SignUpResponseDto(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
)
