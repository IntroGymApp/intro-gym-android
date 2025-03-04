package ru.lonelywh1te.introgym.features.guide.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseItem
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseListUseCase

class ExerciseListFragmentViewModel(
    private val getExerciseListUseCase: GetExerciseListUseCase,
): ViewModel() {
    private val _exerciseItems: MutableStateFlow<List<ExerciseItem>> = MutableStateFlow(emptyList())
    val exerciseItems: StateFlow<List<ExerciseItem>> = _exerciseItems

    private val dispatcher = Dispatchers.IO

    fun getExerciseItems(categoryId: Long) {
        viewModelScope.launch (dispatcher) {
            getExerciseListUseCase(categoryId).collect { list ->
                _exerciseItems.emit(list)
            }
        }
    }
}