package ru.lonelywh1te.introgym.features.sync.data.dto

import com.google.gson.annotations.SerializedName

data class SyncDataDto (

    @SerializedName("workouts")
    val workouts: List<SyncWorkoutDto>,

    @SerializedName("workoutLogs")
    val workoutLogs: List<SyncWorkoutLogDto>,

    @SerializedName("workoutExercises")
    val workoutExercises: List<SyncWorkoutExerciseDto>,

    @SerializedName("workoutExercisePlans")
    val workoutExercisePlans: List<SyncWorkoutExercisePlanDto>,

    @SerializedName("exerciseSets")
    val exerciseSets: List<SyncExerciseSetDto>,

)