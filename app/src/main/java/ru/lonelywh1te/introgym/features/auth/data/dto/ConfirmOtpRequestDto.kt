package ru.lonelywh1te.introgym.features.auth.data.dto

import com.google.gson.annotations.SerializedName

data class ConfirmOtpRequestDto (
    @SerializedName("sessionId")
    val sessionId: String,
    @SerializedName("otp")
    val otp: String,
)