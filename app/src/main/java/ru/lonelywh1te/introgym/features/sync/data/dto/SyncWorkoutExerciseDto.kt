package ru.lonelywh1te.introgym.features.sync.data.dto

import com.google.gson.annotations.SerializedName

data class SyncWorkoutExerciseDto(

    @SerializedName("id")
    val id: Long,

    @SerializedName("workoutId")
    val workoutId: String,

    @SerializedName("exerciseId")
    val exerciseId: String,

    @SerializedName("endDateTime")
    val endDateTime: String,

    @SerializedName("workoutIndex")
    val order: Int,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("lastUpdated")
    val lastUpdated: String,

)
