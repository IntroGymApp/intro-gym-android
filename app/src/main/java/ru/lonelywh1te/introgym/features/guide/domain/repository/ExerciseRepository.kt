package ru.lonelywh1te.introgym.features.guide.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.features.guide.domain.model.Exercise
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseItem

interface ExerciseRepository {
    fun getExerciseById(exerciseId: Long): Flow<Exercise>
    fun getExercisesWithTags(): Flow<List<ExerciseItem>>
}