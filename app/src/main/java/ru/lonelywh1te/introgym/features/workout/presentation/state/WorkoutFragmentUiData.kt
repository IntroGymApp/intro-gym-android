package ru.lonelywh1te.introgym.features.workout.presentation.state

import ru.lonelywh1te.introgym.features.workout.domain.model.workout_log.WorkoutLog
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_log.WorkoutLogState
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutResult
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem

enum class WorkoutFragmentState {
    WORKOUT_TEMPLATE,
    WORKOUT_NOT_STARTED,
    WORKOUT_IN_PROGRESS,
    WORKOUT_FINISHED,
}

data class WorkoutFragmentUiData(
    val workout: Workout? = null,
    val workoutLog: WorkoutLog? = null,
    val workoutExerciseItems: List<WorkoutExerciseItem>? = null,
    val workoutResult: WorkoutResult? = null,
) {
    val state: WorkoutFragmentState get() {
        return when(workoutLog?.state) {
            null -> WorkoutFragmentState.WORKOUT_TEMPLATE
            is WorkoutLogState.NotStarted -> WorkoutFragmentState.WORKOUT_NOT_STARTED
            is WorkoutLogState.InProgress -> WorkoutFragmentState.WORKOUT_IN_PROGRESS
            is WorkoutLogState.Finished -> WorkoutFragmentState.WORKOUT_FINISHED
        }
    }
}

