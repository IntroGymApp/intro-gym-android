package ru.lonelywh1te.introgym.features.workout.domain.model.workout

import java.time.LocalDateTime
import java.util.UUID

data class Workout (
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String = "",
    val isTemplate: Boolean,
    val order: Int,

    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastUpdated: LocalDateTime = LocalDateTime.now(),
)