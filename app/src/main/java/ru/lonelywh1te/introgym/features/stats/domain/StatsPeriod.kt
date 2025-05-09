package ru.lonelywh1te.introgym.features.stats.domain

import java.time.DayOfWeek
import java.time.LocalDate

sealed class StatsPeriod(
    open val startLocalDate: LocalDate,
    open val endLocalDate: LocalDate,
) {
    data class Week(
        override val startLocalDate: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY),
        override val endLocalDate: LocalDate = startLocalDate.plusDays(6)
    ): StatsPeriod(startLocalDate, endLocalDate)

    data class Month(
        override val startLocalDate: LocalDate = LocalDate.now().withDayOfMonth(1),
        override val endLocalDate: LocalDate = startLocalDate.withDayOfMonth(startLocalDate.lengthOfMonth())
    ): StatsPeriod(startLocalDate, endLocalDate)

    data class Year(
        override val startLocalDate: LocalDate = LocalDate.now().withDayOfYear(1),
        override val endLocalDate: LocalDate = startLocalDate.withDayOfYear(startLocalDate.lengthOfYear())
    ): StatsPeriod(startLocalDate, endLocalDate)

}