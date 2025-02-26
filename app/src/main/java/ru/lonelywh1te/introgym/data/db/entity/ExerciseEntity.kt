package ru.lonelywh1te.introgym.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.lonelywh1te.introgym.data.db.converters.StepsConverter
import ru.lonelywh1te.introgym.data.db.converters.TipsConverter

@Entity(tableName = "exercise")

data class ExerciseEntity(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "steps")
    @field:TypeConverters(StepsConverter::class)
    val steps: List<String>,
    @ColumnInfo(name = "tips")
    @field:TypeConverters(TipsConverter::class)
    val tips: List<String>,
    @ColumnInfo(name = "img_filename")
    val imgFilename: String,
    @ColumnInfo(name = "anim_filename")
    val animFilename: String,
)