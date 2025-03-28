package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout

import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository

class DeleteWorkoutUseCase(private val repository: WorkoutRepository) {
    suspend operator fun invoke(id: Long){
        repository.deleteWorkout(id)

        val workouts = repository.getWorkouts().toMutableList()
        workouts
            .mapIndexed { index, workout -> workout.copy(order = index) }
            .forEach {
                repository.updateWorkout(it)
            }
    }
}