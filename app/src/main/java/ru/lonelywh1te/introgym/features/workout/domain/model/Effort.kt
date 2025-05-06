package ru.lonelywh1te.introgym.features.workout.domain.model

enum class Effort(private val percent: Int) {
    WARMUP(0),
    LOW(25),
    MEDIUM(50),
    HARD(75),
    MAX(100);

    fun toPercent() = percent

    companion object {
        fun fromPercent(percent: Int?): Effort? {
            return when(percent) {
                0 -> WARMUP
                25 -> LOW
                50 -> MEDIUM
                75 -> HARD
                100 -> MAX
                null -> null
                else -> throw IllegalArgumentException("Unsupported effort percent: $percent")
            }
        }
    }
}