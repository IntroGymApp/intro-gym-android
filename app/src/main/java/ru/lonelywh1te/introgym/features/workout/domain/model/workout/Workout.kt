package ru.lonelywh1te.introgym.features.workout.domain.model.workout

import java.time.LocalDateTime

data class Workout (
    val id: Long,
    val name: String,
    val description: String,
    val isTemplate: Boolean,
    val order: Int,

    val createdAt: LocalDateTime,
    val lastUpdated: LocalDateTime,
) {
    companion object {
        fun empty(isTemplate: Boolean) = Workout(
            id = -1L,
            name = "",
            description = "",
            isTemplate = isTemplate,
            order = -1,
            createdAt = LocalDateTime.now(),
            lastUpdated = LocalDateTime.now(),
        )
    }
}