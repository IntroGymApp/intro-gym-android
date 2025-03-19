package ru.lonelywh1te.introgym.data.db.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import ru.lonelywh1te.introgym.data.db.entity.ExerciseEntity
import ru.lonelywh1te.introgym.data.db.entity.TagEntity
import ru.lonelywh1te.introgym.data.db.entity.TagToExerciseEntity

data class ExerciseEntityWithTags (
    @Embedded val exercise: ExerciseEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = TagEntity::class,
        associateBy = Junction(
            value = TagToExerciseEntity::class,
            parentColumn = "exercise_id",
            entityColumn = "tag_id"
        )
    )
    val tags: List<TagEntity>,
)