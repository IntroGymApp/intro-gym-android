package ru.lonelywh1te.introgym.features.stats.domain.model

import java.time.DayOfWeek
import java.time.LocalDate

sealed class StatsPeriod(
    open val startLocalDate: LocalDate,
    open val endLocalDate: LocalDate,
    open val size: Long = endLocalDate.toEpochDay() - startLocalDate.toEpochDay() + 1
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

    companion object {
        fun generateDates(period: StatsPeriod): List<LocalDate> {
            return when (period) {
                is StatsPeriod.Week -> {
                    val startDate = period.startLocalDate
                    (0 until 7).map { startDate.plusDays(it.toLong()) }
                }
                is StatsPeriod.Month -> {
                    val startDate = period.startLocalDate
                    val endDate = period.endLocalDate
                    generateSequence(startDate) { it.plusDays(1) }
                        .takeWhile { !it.isAfter(endDate) }
                        .toList()
                }
                is StatsPeriod.Year -> {
                    val startDate = period.startLocalDate
                    val endDate = period.endLocalDate
                    generateSequence(startDate) { it.plusDays(1) }
                        .takeWhile { !it.isAfter(endDate) }
                        .toList()
                }
            }
        }
    }
}