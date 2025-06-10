package ru.lonelywh1te.introgym.data.db.converters

import androidx.room.TypeConverter
import java.util.UUID

class UUIDConverter {

    @TypeConverter
    fun toString(uuid: UUID): String = uuid.toString()

    @TypeConverter
    fun toUUID(string: String): UUID = UUID.fromString(string)

}