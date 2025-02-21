package ru.lonelywh1te.introgym.auth.data.dto.otp_send

import com.google.gson.annotations.SerializedName

data class SendOtpResponseDto(
    @SerializedName("sessionId")
    val sessionId: String,
)
