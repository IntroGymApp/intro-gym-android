package ru.lonelywh1te.introgym.features.workout.domain.model

enum class Effort(private val percent: Int) {
    WARMUP(0),
    LOW(25),
    MEDIUM(50),
    HARD(75),
    MAX(100);

    fun toPercent() = percent
}