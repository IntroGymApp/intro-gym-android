package ru.lonelywh1te.introgym.auth.data.dto.refresh_tokens

import com.google.gson.annotations.SerializedName

data class RefreshTokensResponseDto(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
)
