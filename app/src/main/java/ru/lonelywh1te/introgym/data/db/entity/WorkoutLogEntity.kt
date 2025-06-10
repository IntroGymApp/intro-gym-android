package ru.lonelywh1te.introgym.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity(
    tableName = "workout_log",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workout_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
    ]
)
data class WorkoutLogEntity(

    @ColumnInfo(name = "id")
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "workout_id")
    val workoutId: UUID,

    @ColumnInfo(name = "date")
    val date: LocalDate,

    @ColumnInfo(name = "start_datetime")
    val startDateTime: LocalDateTime? = null,

    @ColumnInfo(name = "end_datetime")
    val endDateTime: LocalDateTime? = null,

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