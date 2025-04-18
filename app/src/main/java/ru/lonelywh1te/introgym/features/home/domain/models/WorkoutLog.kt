package ru.lonelywh1te.introgym.features.home.domain.models

import java.time.LocalDateTime

data class WorkoutLog (
    val id: Long = 0L,
    val workoutId: Long,
    val startDateTime: LocalDateTime? = null,
    val endDateTime: LocalDateTime? = null,
    val order: Int,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastUpdatedAt: LocalDateTime = LocalDateTime.now(),
)
