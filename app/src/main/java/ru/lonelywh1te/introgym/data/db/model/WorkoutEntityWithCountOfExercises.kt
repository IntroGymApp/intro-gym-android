package ru.lonelywh1te.introgym.data.db.model

import androidx.room.ColumnInfo
import java.time.LocalDateTime

data class WorkoutEntityWithCountOfExercises (

    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo("is_template")
    val isTemplate: Boolean = false,

    @ColumnInfo(name = "order")
    val order: Int,

    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,

    @ColumnInfo(name = "last_updated")
    val lastUpdated: LocalDateTime,

    @ColumnInfo(name = "is_synchronized")
    val isSynchronized: Boolean = false,

    @ColumnInfo(name = "count_of_exercises")
    val countOfExercises: Int,

)