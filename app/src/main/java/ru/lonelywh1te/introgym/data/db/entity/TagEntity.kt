package ru.lonelywh1te.introgym.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.lonelywh1te.introgym.data.db.TagType
import ru.lonelywh1te.introgym.data.db.converters.TagTypeConverter

@Entity("tag")
data class TagEntity(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "type")
    @field:TypeConverters(TagTypeConverter::class)
    val type: TagType,
)
