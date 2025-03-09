package ru.lonelywh1te.introgym.features.auth.data.dto

import com.google.gson.annotations.SerializedName

data class ChangePasswordResponseDto (
    @SerializedName("isSuccess")
    val isSuccess: Boolean,
)