package ru.lonelywh1te.introgym.features.workout.domain.model.workout

import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem
import java.time.LocalDate

data class Workout (
    val id: Long,
    val name: String,
    val description: String,
    val date: LocalDate? = null,
    val exercises: List<WorkoutExercise>,
)