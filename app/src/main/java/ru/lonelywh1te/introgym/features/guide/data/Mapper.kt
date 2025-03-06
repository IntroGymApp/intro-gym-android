package ru.lonelywh1te.introgym.features.guide.data

import ru.lonelywh1te.introgym.data.db.entity.ExerciseEntity
import ru.lonelywh1te.introgym.data.db.entity.TagEntity
import ru.lonelywh1te.introgym.data.db.model.ExerciseCategoryWithCount
import ru.lonelywh1te.introgym.features.guide.domain.model.Exercise
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseCategoryItem
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseItem
import ru.lonelywh1te.introgym.features.guide.domain.model.Tag

fun ExerciseCategoryWithCount.toExerciseCategoryItem(): ExerciseCategoryItem {
    return ExerciseCategoryItem(
        id = this.id,
        name = this.name,
        countOfExercises = this.countOfExercises,
        imgFilename = this.imgFilename,
    )
}

fun ExerciseEntity.toExerciseItem(): ExerciseItem {
    return ExerciseItem(
        id = this.id,
        name = this.name,
        imgFilename = this.imgFilename,
    )
}

fun ExerciseEntity.toExercise(): Exercise {
    return Exercise(
        id = this.id,
        categoryId = this.categoryId,
        name = this.name,
        description = this.description,
        steps = this.steps,
        tips = this.tips,
        animFilename = this.animFilename,
    )
}

fun TagEntity.toTag(): Tag {
    return Tag(
        id = this.id,
        name = this.name,
        type = this.type,
    )
}