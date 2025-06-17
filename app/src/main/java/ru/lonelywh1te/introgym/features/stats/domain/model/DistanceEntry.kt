package ru.lonelywh1te.introgym.features.stats.domain.model

import java.time.LocalDate

data class DistanceEntry(
    val date: LocalDate,
    val distance: Int,
)