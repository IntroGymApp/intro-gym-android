package ru.lonelywh1te.introgym.features.guide.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.features.guide.domain.model.Exercise
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseUseCase

// TODO: replace null на state?

class ExerciseFragmentViewModel(
    private val getExerciseUseCase: GetExerciseUseCase,
): ViewModel() {
    private val _exercise: MutableStateFlow<Exercise?> = MutableStateFlow(null)
    val exercise: StateFlow<Exercise?> get() = _exercise

    private val dispatcher = Dispatchers.IO

    fun getExercise(exerciseId: Long) {
        viewModelScope.launch (dispatcher) {
            getExerciseUseCase(exerciseId).collect { exercise ->
                _exercise.emit(exercise)
            }
        }
    }
}