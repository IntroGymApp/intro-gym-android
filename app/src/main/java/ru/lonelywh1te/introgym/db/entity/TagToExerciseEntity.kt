package ru.lonelywh1te.introgym.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    "tag_to_exercise",
    foreignKeys = [
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tag_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
    ]
)
data class TagToExerciseEntity (
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "tag_id")
    val tagId: Int,
    @ColumnInfo(name = "exercise_id")
    val exerciseId: Int,
    @ColumnInfo(name = "is_main")
    val isMain: Boolean,
)