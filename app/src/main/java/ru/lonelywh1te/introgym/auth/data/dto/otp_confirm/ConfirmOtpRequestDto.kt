package ru.lonelywh1te.introgym.auth.data.dto.otp_confirm

import com.google.gson.annotations.SerializedName

data class ConfirmOtpRequestDto (
    @SerializedName("sessionId")
    val sessionId: String,
    @SerializedName("otp")
    val otp: String,
)