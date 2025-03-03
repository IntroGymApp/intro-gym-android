package ru.lonelywh1te.introgym.features.guide.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseCategoryItem
import ru.lonelywh1te.introgym.features.guide.domain.repository.ExerciseCategoryRepository

class GetExerciseCategoriesUseCase(private val repository: ExerciseCategoryRepository) {
    operator fun invoke(): Flow<List<ExerciseCategoryItem>> {
        return repository.getCategories()
    }
}