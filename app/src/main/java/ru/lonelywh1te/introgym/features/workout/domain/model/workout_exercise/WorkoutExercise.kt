package ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.util.UUID

@Parcelize
data class WorkoutExercise (
    val id: UUID = UUID.randomUUID(),
    val workoutId: UUID,
    val exerciseId: Long,
    val order: Int,
    val comment: String = "",

    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastUpdated: LocalDateTime = LocalDateTime.now(),
): Parcelable