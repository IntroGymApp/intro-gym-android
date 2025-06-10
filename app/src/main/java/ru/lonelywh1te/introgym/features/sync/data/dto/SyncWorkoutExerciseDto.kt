package ru.lonelywh1te.introgym.features.sync.data.dto

import com.google.gson.annotations.SerializedName

data class SyncWorkoutExerciseDto(

    @SerializedName("id")
    val id: String,

    @SerializedName("workoutId")
    val workoutId: String,

    @SerializedName("exerciseId")
    val exerciseId: Long,

    @SerializedName("workoutIndex")
    val order: Int,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("lastUpdated")
    val lastUpdated: String,

)
