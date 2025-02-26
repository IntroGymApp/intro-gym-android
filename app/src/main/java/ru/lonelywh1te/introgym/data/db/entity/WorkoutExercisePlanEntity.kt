package ru.lonelywh1te.introgym.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.lonelywh1te.introgym.data.db.UploadStatus

@Entity(
    "workout_exercise_plan",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["workout_exercise_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        )
    ]
)

data class WorkoutExercisePlanEntity(
    @ColumnInfo(name = "id") @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "workout_exercise_id")
    val workoutExerciseId: Long,
    @ColumnInfo(name = "sets")
    val sets: Int?,
    @ColumnInfo(name = "reps")
    val reps: Int?,
    @ColumnInfo(name = "weight")
    val weight: Int?,
    @ColumnInfo(name = "time_in_sec")
    val timeInSec: Int?,
    @ColumnInfo(name = "distance_in_meters")
    val distanceInMeters: Int?,
    @ColumnInfo(name = "upload_status")
    val uploadStatus: UploadStatus = UploadStatus.NONE,
)
