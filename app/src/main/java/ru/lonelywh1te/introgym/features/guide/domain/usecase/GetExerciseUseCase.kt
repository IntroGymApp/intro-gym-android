package ru.lonelywh1te.introgym.features.guide.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.features.guide.domain.model.Exercise
import ru.lonelywh1te.introgym.features.guide.domain.repository.ExerciseRepository

class GetExerciseUseCase(private val repository: ExerciseRepository) {
    operator fun invoke(exerciseId: Long): Flow<Exercise> {
        return repository.getExerciseById(exerciseId)
    }
}