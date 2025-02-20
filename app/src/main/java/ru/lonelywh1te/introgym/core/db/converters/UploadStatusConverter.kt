package ru.lonelywh1te.introgym.core.db.converters

import androidx.room.TypeConverter
import ru.lonelywh1te.introgym.core.db.UploadStatus

class UploadStatusConverter {
    @TypeConverter
    fun toString(type: UploadStatus): String = type.name

    @TypeConverter
    fun toUploadStatus(name: String): UploadStatus = enumValueOf<UploadStatus>(name)
}