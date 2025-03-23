package ru.lonelywh1te.introgym.features.workout.domain.model.workout

import java.time.LocalDateTime

data class Workout (
    val id: Long = -1,
    val name: String,
    val description: String,
    val isTemplate: Boolean,
    val order: Int = -1,

    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastUpdated: LocalDateTime = LocalDateTime.now(),
)