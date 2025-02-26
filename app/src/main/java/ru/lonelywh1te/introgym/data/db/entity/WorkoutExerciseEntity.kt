package ru.lonelywh1te.introgym.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.lonelywh1te.introgym.data.db.UploadStatus

@Entity(
    "workout_exercise",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workout_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class WorkoutExerciseEntity(
    @ColumnInfo(name = "id") @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "workout_id")
    val workoutId: Long,
    @ColumnInfo(name = "exercise_id")
    val exerciseId: Int,
    @ColumnInfo(name = "execution_order")
    val executionOrder: Int,
    @ColumnInfo(name = "upload_status")
    val uploadStatus: UploadStatus = UploadStatus.NONE,
)
