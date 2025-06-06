package ru.lonelywh1te.introgym.features.profile.data.dto

import com.google.gson.annotations.SerializedName

data class UserProfileDataDto (

    @SerializedName("username")
    val username: String,

    @SerializedName("joinedAt")
    val joinedAt: String,

)