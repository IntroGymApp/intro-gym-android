package ru.lonelywh1te.introgym.features.workout.domain.model.workout

import java.time.LocalTime

data class WorkoutResult(
    val totalTime: LocalTime,
    val progress: Int,
    val totalWeight: Float,
    val totalEffort: Int,
)
