package ru.lonelywh1te.introgym.features.guide.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.data.db.dao.ExerciseDao
import ru.lonelywh1te.introgym.features.guide.data.toExercise
import ru.lonelywh1te.introgym.features.guide.data.toExerciseItem
import ru.lonelywh1te.introgym.features.guide.domain.model.Exercise
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseItem
import ru.lonelywh1te.introgym.features.guide.domain.repository.ExerciseRepository

class ExerciseRepositoryImpl(
    private val exerciseDao: ExerciseDao
): ExerciseRepository {

    override fun getExerciseById(exerciseId: Long): Flow<Exercise> {
        return exerciseDao.getExerciseById(exerciseId).map { exerciseEntity ->
            exerciseEntity.toExercise()
        }
    }

    override fun getExercisesWithTags(): Flow<List<ExerciseItem>> {
        return exerciseDao.getExercisesWithTags().map { exerciseEntitiesWithTags ->
            exerciseEntitiesWithTags.map { it.toExerciseItem() }
        }
    }

}