package ru.lonelywh1te.introgym.features.guide.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseItem
import ru.lonelywh1te.introgym.features.guide.domain.repository.ExerciseRepository

class SearchExercisesByNameUseCase(private val repository: ExerciseRepository) {
    operator fun invoke(query: String): Flow<List<ExerciseItem>> {
        return repository.getExercisesWithTags().map { list ->
            list.filter { it.name.contains(query, ignoreCase = true) }
        }
    }
}