package ru.lonelywh1te.introgym.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.lonelywh1te.introgym.data.db.UploadStatus

@Entity(
    "exercise_set",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workout_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onUpdate = ForeignKey.NO_ACTION,
            onDelete = ForeignKey.NO_ACTION,
        ),
    ]
)

data class ExerciseSetEntity(
    @ColumnInfo(name = "id") @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "workout_id")
    val workoutId: Long,
    @ColumnInfo(name = "exercise_id")
    val exerciseId: Long,
    @ColumnInfo(name = "reps")
    val reps: Int?,
    @ColumnInfo(name = "weight")
    val weight: Int?,
    @ColumnInfo(name = "time_in_sec")
    val timeInSec: Int?,
    @ColumnInfo(name = "distance_in_meters")
    val distanceInMeters: Int?,
    @ColumnInfo(name = "effort")
    val effort: Int?,
    @ColumnInfo(name = "upload_status")
    val uploadStatus: UploadStatus = UploadStatus.NONE,
)
