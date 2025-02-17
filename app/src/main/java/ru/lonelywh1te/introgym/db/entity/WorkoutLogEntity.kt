package ru.lonelywh1te.introgym.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.lonelywh1te.introgym.db.UploadStatus
import java.time.LocalDate

@Entity("workout_log")
data class WorkoutLogEntity(
    @ColumnInfo(name = "id")
    @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "date")
    val date: LocalDate,
    @ColumnInfo(name = "execution_time_in_sec")
    val executionTimeInSec: Int?,
    @ColumnInfo(name = "upload_status")
    val uploadStatus: UploadStatus = UploadStatus.NONE,
)