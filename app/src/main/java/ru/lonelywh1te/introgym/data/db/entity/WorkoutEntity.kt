package ru.lonelywh1te.introgym.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

@Entity(
    tableName = "workout"
)
data class WorkoutEntity(

    @ColumnInfo(name = "id")
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),

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

    @ColumnInfo(name = "updated_at")
    val lastUpdated: LocalDateTime,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @ColumnInfo(name = "is_synchronized")
    val isSynchronized: Boolean = false,
)