package ru.lonelywh1te.introgym.features.guide.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.data.db.dao.ExerciseCategoryDao
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseCategoryItem
import ru.lonelywh1te.introgym.features.guide.domain.repository.ExerciseCategoryRepository

class ExerciseCategoryRepositoryImpl(
    private val exerciseCategoryDao: ExerciseCategoryDao,
): ExerciseCategoryRepository {

    override fun getCategories(): Flow<List<ExerciseCategoryItem>> {
        return exerciseCategoryDao.getCategoriesWithExerciseCount().map { categories ->
            categories.map { it.toExerciseCategoryItem() }
        }
    }

}