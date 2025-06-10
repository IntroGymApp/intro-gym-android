package ru.lonelywh1te.introgym.features.home.domain.models

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class WorkoutLog (
    val id: UUID? = null,
    val workoutId: UUID,
    val date: LocalDate,
    val startDateTime: LocalDateTime? = null,
    val endDateTime: LocalDateTime? = null,
    val order: Int = -1,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastUpdatedAt: LocalDateTime = LocalDateTime.now(),
    val isSynchronized: Boolean = false,
)
