package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutItem
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository

class GetWorkoutListUseCase(private val repository: WorkoutRepository) {
    operator fun invoke(): Flow<List<WorkoutItem>> {
        return repository.getWorkoutItems().map { list ->
            list.sortedBy { it.order }
        }
    }
}