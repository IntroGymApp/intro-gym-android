package ru.lonelywh1te.introgym.features.guide.presentation.exercises.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseCategoryItem
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseItem
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseCategoriesUseCase
import ru.lonelywh1te.introgym.features.guide.domain.usecase.SearchExercisesByNameUseCase

class ExerciseCategoriesFragmentViewModel(
    private val getExerciseCategoriesUseCase: GetExerciseCategoriesUseCase,
    private val searchExercisesByNameUseCase: SearchExercisesByNameUseCase,
): ViewModel() {
    private val _categories: MutableStateFlow<List<ExerciseCategoryItem>> = MutableStateFlow(emptyList())
    val categories: StateFlow<List<ExerciseCategoryItem>> get() = _categories

    private val _searchExercisesResult: MutableStateFlow<List<ExerciseItem>> = MutableStateFlow(emptyList())
    val searchExercisesResult: StateFlow<List<ExerciseItem>> get() = _searchExercisesResult

    private val dispatcher = Dispatchers.IO

    fun searchExercisesByName(searchQuery: String) {
        viewModelScope.launch (dispatcher) {
            searchExercisesByNameUseCase(searchQuery).collect { result ->
                _searchExercisesResult.emit(result)
            }
        }
    }

    init {
        viewModelScope.launch (dispatcher) {
            getExerciseCategoriesUseCase().collect {
                _categories.value = it
            }
        }
    }
}