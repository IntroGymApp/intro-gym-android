package ru.lonelywh1te.introgym.data.db.model

import androidx.room.ColumnInfo

data class ExerciseInfo (

    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "img_filename")
    val imgFilename: String,

    @ColumnInfo(name = "anim_filename")
    val animFilename: String,

)