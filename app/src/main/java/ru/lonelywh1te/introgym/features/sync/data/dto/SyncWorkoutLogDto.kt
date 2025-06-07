package ru.lonelywh1te.introgym.features.sync.data.dto

import com.google.gson.annotations.SerializedName

data class SyncWorkoutLogDto(

    @SerializedName("id")
    val id: Long,

    @SerializedName("workoutId")
    val workoutId: String,

    @SerializedName("date")
    val date: String,

    @SerializedName("startDateTime")
    val startDateTime: String,

    @SerializedName("endDateTime")
    val endDateTime: String,

    @SerializedName("workoutLogIndex")
    val order: Int,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("lastUpdated")
    val lastUpdated: String,

)
