package ru.lonelywh1te.introgym.features.guide.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseCategoryItem
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseCategoriesUseCase

class ExerciseCategoriesViewModel(
    private val getExerciseCategoriesUseCase: GetExerciseCategoriesUseCase
): ViewModel() {
    private val _categories: MutableStateFlow<List<ExerciseCategoryItem>> = MutableStateFlow(emptyList())
    val categories: StateFlow<List<ExerciseCategoryItem>> get() = _categories

    init {
        viewModelScope.launch {
            getExerciseCategoriesUseCase().collect {
                _categories.value = it
            }
        }
    }
}