package ru.lonelywh1te.introgym.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "workout"
)
data class WorkoutEntity(

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
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
)