package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout

import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository

class DeleteWorkoutUseCase(private val repository: WorkoutRepository) {
    suspend operator fun invoke(id: Long){
        repository.deleteWorkout(id)
    }
}