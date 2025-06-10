package ru.lonelywh1te.introgym.features.sync.data.dto

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class SyncWorkoutLogDto(

    @SerializedName("id")
    val id: String,

    @SerializedName("workoutId")
    val workoutId: String,

    @SerializedName("date")
    val date: String,

    @SerializedName("startDatetime")
    val startDateTime: String,

    @SerializedName("endDatetime")
    val endDateTime: String,

    @SerializedName("workoutLogIndex")
    val order: Int,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("lastUpdated")
    val lastUpdated: String,

)
