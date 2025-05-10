package ru.lonelywh1te.introgym.features.stats.domain.model

import java.time.LocalDate

data class WeightEntry(
    val date: LocalDate,
    val weight: Float,
)
