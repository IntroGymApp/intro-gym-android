package ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise

import java.time.LocalDateTime

data class WorkoutExercise (
    val id: Long,
    val workoutId: Long,
    val exerciseId: Long,
    val order: Int,
    val comment: String,

    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastUpdated: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun empty(id: Long = -1L, exerciseId: Long = -1L, order: Int = -1) = WorkoutExercise(
            id = id,
            workoutId = 1L,
            exerciseId = exerciseId,
            order = order,
            comment = "",
        )
    }
}