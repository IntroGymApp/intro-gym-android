package ru.lonelywh1te.introgym.features.sync.data.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class SyncWorkoutExercisePlanDto(

    @SerializedName("id")
    val id: String,

    @SerializedName("exerciseId")
    val workoutExerciseId: String,

    @SerializedName("sets")
    val sets: Int?,

    @SerializedName("reps")
    val reps: Int?,

    @SerializedName("weightKg")
    val weightKg: Float?,

    @SerializedName("timeInSec")
    val timeInSec: Int?,

    @SerializedName("distanceInMeters")
    val distanceInMeters: Int?,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("lastUpdated")
    val lastUpdated: String,

)

