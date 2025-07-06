package ru.lonelywh1te.introgym.features.home.domain.models

import ru.lonelywh1te.introgym.features.workout.domain.model.workout_log.WorkoutLogState
import java.util.UUID

data class WorkoutLogItem (
    val workoutLogId: UUID,
    val workoutId: UUID,
    val state: WorkoutLogState,
    val workoutName: String,
    val workoutDescription: String,
    val countOfExercises: Int,
    val order: Int,
)