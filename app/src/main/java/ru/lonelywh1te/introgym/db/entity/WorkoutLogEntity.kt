package ru.lonelywh1te.introgym.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.lonelywh1te.introgym.db.UploadStatus
import java.time.LocalDate
import java.time.LocalDateTime

@Entity("workout_log")
data class WorkoutLogEntity(
    @ColumnInfo(name = "id")
    @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "date")
    val date: LocalDate,
    @ColumnInfo(name = "start_datetime")
    val startDateTime: LocalDateTime? = null,
    @ColumnInfo(name = "end_datetime")
    val endDateTime: LocalDateTime? = null,
    @ColumnInfo(name = "upload_status")
    val uploadStatus: UploadStatus = UploadStatus.NONE,
)