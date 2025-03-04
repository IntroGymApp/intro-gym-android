package ru.lonelywh1te.introgym.features.guide.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.data.db.dao.ExerciseDao
import ru.lonelywh1te.introgym.features.guide.domain.model.Exercise
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseItem
import ru.lonelywh1te.introgym.features.guide.domain.repository.ExerciseRepository

class ExerciseRepositoryImpl(
    private val exerciseDao: ExerciseDao
): ExerciseRepository {
    override fun getExerciseById(exerciseId: Long): Flow<Exercise> {
        return exerciseDao.getExerciseById(exerciseId).map { exerciseEntity ->
            Exercise(
                id = exerciseEntity.id,
                categoryId = exerciseEntity.categoryId,
                name = exerciseEntity.name,
                description = exerciseEntity.description,
                steps = exerciseEntity.steps,
                tips = exerciseEntity.tips,
                animFilename = exerciseEntity.animFilename,
            )
        }
    }

    override fun getExercisesByCategoryId(categoryId: Long): Flow<List<ExerciseItem>> {
        return exerciseDao.getExerciseEntitiesByCategoryId(categoryId).map { exerciseEntities ->
            exerciseEntities.map {
                ExerciseItem(
                    id = it.id,
                    name = it.name,
                    imgFilename = it.imgFilename,
                )
            }
        }
    }

    override fun searchExercisesByName(query: String): Flow<List<ExerciseItem>> {
        return exerciseDao.searchExercicisesByName(query).map { exerciseEntities ->
            exerciseEntities.map {
                ExerciseItem(
                    id = it.id,
                    name = it.name,
                    imgFilename = it.imgFilename,
                )
            }
        }
    }
}