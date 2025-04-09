package ru.lonelywh1te.introgym.features.workout.domain.model.workout

import java.time.LocalDateTime

data class Workout (
    val id: Long = 0L,
    val name: String,
    val description: String = "",
    val isTemplate: Boolean,
    val order: Int,

    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastUpdated: LocalDateTime = LocalDateTime.now(),
)