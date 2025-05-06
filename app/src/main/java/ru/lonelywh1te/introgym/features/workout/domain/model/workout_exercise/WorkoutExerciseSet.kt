package ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise

data class WorkoutExerciseSet(
    val workoutExerciseId: Long = 0L,
    val reps: Int? = null,
    val weightKg: Int? = null,
    val timeInSeconds: Int? = null,
    val distanceInMeters: Int? = null,
    val effort: Int? = null,
)
