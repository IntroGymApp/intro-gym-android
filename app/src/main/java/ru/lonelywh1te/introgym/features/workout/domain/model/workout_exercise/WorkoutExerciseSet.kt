package ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise

import ru.lonelywh1te.introgym.features.workout.domain.model.Effort
import java.time.LocalDateTime
import java.util.UUID

data class WorkoutExerciseSet(
    val id: UUID = UUID.randomUUID(),
    val workoutExerciseId: UUID,
    val reps: Int? = null,
    val weightKg: Float? = null,
    val timeInSeconds: Int? = null,
    val distanceInMeters: Int? = null,
    val effort: Effort? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastUpdated: LocalDateTime = LocalDateTime.now(),
)
