package ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise

import java.time.LocalDateTime

data class WorkoutExercise (
    val id: Long = 0,
    val exerciseId: Long,
    val comment: String,
    val plan: WorkoutExercisePlan,

    val createdAt: LocalDateTime,
    val lastUpdated: LocalDateTime,
)