package ru.lonelywh1te.introgym.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

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
    val id: Long,

    @ColumnInfo(name = "workout_id")
    val workoutId: Long,

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

    @ColumnInfo(name = "last_updated")
    val lastUpdated: LocalDateTime,

    @ColumnInfo(name = "is_synchronized")
    val isSynchronized: Boolean = false,

)