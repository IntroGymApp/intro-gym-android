package ru.lonelywh1te.introgym.core.ui.utils

import java.time.format.DateTimeFormatter

object DateAndTimeStringFormatUtils {
    val fullTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
}