package ru.lonelywh1te.introgym.auth.data.dto.otp_confirm

import com.google.gson.annotations.SerializedName

data class ConfirmOtpResponseDto(
    @SerializedName("isSuccess")
    val isSuccess: Boolean,
)
