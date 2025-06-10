package ru.lonelywh1te.introgym.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

@Entity(
    "exercise_set",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["workout_exercise_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)

data class ExerciseSetEntity(

    @ColumnInfo(name = "id")
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "workout_exercise_id")
    val workoutExerciseId: UUID,

    @ColumnInfo(name = "reps")
    val reps: Int?,

    @ColumnInfo(name = "weight_kg")
    val weightKg: Float?,

    @ColumnInfo(name = "time_in_sec")
    val timeInSec: Int?,

    @ColumnInfo(name = "distance_in_meters")
    val distanceInMeters: Int?,

    @ColumnInfo(name = "effort")
    val effort: Int?,

    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,

    @ColumnInfo(name = "updated_at")
    val lastUpdated: LocalDateTime,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @ColumnInfo(name = "is_synchronized")
    val isSynchronized: Boolean = false,
)
