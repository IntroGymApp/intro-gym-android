package ru.lonelywh1te.introgym.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

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
    ]
)

data class ExerciseSetEntity(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(name = "workout_exercise_id")
    val workoutExerciseId: Long,

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

    @ColumnInfo(name = "last_updated")
    val lastUpdated: LocalDateTime,

    @ColumnInfo(name = "is_synchronized")
    val isSynchronized: Boolean = false,
)
