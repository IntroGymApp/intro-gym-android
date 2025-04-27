package ru.lonelywh1te.introgym.features.home.domain.models

data class WorkoutLogItem (
    val workoutLogId: Long,
    val workoutId: Long,
    val state: WorkoutLogState,
    val workoutName: String,
    val workoutDescription: String,
    val countOfExercises: Int,
    val order: Int,
)