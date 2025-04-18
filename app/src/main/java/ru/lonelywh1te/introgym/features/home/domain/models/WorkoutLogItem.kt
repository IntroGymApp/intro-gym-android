package ru.lonelywh1te.introgym.features.home.domain.models

data class WorkoutLogItem (
    val workoutLogId: Long,
    val workoutId: Long,
    val state: WorkoutLogState,
    val order: Int,
)