package ru.lonelywh1te.introgym.features.auth.data.dto

import com.google.gson.annotations.SerializedName

data class SignInRequestDto(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
)
