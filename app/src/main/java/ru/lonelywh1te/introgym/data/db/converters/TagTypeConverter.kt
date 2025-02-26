package ru.lonelywh1te.introgym.data.db.converters

import androidx.room.TypeConverter
import ru.lonelywh1te.introgym.data.db.TagType

class TagTypeConverter {
    @TypeConverter
    fun toString(type: TagType): String = type.name

    @TypeConverter
    fun toTagType(name: String): TagType = enumValueOf<TagType>(name)
}