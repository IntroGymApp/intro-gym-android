package ru.lonelywh1te.introgym.features.sync.data.dto

import com.google.gson.annotations.SerializedName

data class SyncWorkoutDto(

    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("isTemplate")
    val isTemplate: String,

    @SerializedName("workoutIndex")
    val order: Int,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("lastUpdated")
    val lastUpdated: String,

)
