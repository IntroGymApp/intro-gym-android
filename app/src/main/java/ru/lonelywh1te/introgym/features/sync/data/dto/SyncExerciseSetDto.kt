package ru.lonelywh1te.introgym.features.sync.data.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class SyncExerciseSetDto(

    @SerializedName("id")
    val id: Long,

    @SerializedName("exerciseId")
    val workoutExerciseId: Long,

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
    val createdAt: LocalDateTime,

    @SerializedName("lastUpdated")
    val lastUpdated: LocalDateTime,
)
