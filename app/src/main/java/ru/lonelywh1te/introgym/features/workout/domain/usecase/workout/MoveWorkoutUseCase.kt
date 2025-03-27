package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout

import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository

class MoveWorkoutUseCase(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(from: Int, to: Int) {
        if (from == to) return
        val workouts = repository.getWorkouts().toMutableList()

        if (from < 0 || to < 0 || from >= workouts.size || to >= workouts.size) throw Exception("Invalid indexes")

        val item = workouts.removeAt(from)
        workouts.add(to, item)

        workouts
            .mapIndexed { index, workout -> workout.copy(order = index) }
            .forEach {
                repository.updateWorkout(it)
            }
    }
}