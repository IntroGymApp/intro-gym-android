package ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class WorkoutExercise (
    val id: Long = 0L,
    val workoutId: Long,
    val exerciseId: Long,
    val order: Int,
    val comment: String = "",

    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastUpdated: LocalDateTime = LocalDateTime.now(),
): Parcelable