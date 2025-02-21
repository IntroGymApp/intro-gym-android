package ru.lonelywh1te.introgym.auth.data.dto.sign_up

import com.google.gson.annotations.SerializedName

data class SignUpRequestDto(
    @SerializedName("sessionId")
    val sessionId: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
)
