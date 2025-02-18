package ru.lonelywh1te.introgym.db.converters

import androidx.room.TypeConverter
import java.time.LocalDateTime

class LocalDateTimeConverter {
    @TypeConverter
    fun toString(localDateTime: LocalDateTime): String {
        return localDateTime.toString()
    }

    @TypeConverter
    fun toLocalDate(dateTime: String): LocalDateTime {
        return LocalDateTime.parse(dateTime)
    }
}