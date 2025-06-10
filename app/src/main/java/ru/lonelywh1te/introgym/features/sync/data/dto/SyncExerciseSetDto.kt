package ru.lonelywh1te.introgym.features.sync.data.dto

import com.google.gson.annotations.SerializedName

data class SyncExerciseSetDto(

    @SerializedName("id")
    val id: String,

    @SerializedName("workoutExerciseId")
    val workoutExerciseId: String,

    @SerializedName("reps")
    val reps: Int?,

    @SerializedName("weightKg")
    val weightKg: Float?,

    @SerializedName("timeInSec")
    val timeInSec: Int?,

    @SerializedName("distanceInMeters")
    val distanceInMeters: Int?,

    @SerializedName("effort")
    val effort: Int?,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("lastUpdated")
    val lastUpdated: String,
)
