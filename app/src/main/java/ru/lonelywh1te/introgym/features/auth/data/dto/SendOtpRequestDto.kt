package ru.lonelywh1te.introgym.features.auth.data.dto

import com.google.gson.annotations.SerializedName

data class SendOtpRequestDto (
    @SerializedName("email")
    val email: String,
)

