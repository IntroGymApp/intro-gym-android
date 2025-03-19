package ru.lonelywh1te.introgym.features.guide.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseItem
import ru.lonelywh1te.introgym.features.guide.domain.repository.ExerciseRepository

class GetExercisesWithSelectedTagsUseCase(private val repository: ExerciseRepository) {
    operator fun invoke(tagsIds: List<Int>): Flow<List<ExerciseItem>> {
        return repository.filterExerciseByTags(tagsIds)
    }
}