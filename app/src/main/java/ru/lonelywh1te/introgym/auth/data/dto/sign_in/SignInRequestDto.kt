package ru.lonelywh1te.introgym.auth.data.dto.sign_in

import com.google.gson.annotations.SerializedName

data class SignInRequestDto(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
)
