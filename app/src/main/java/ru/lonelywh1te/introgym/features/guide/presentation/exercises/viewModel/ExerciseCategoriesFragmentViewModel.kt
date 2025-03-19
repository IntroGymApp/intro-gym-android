package ru.lonelywh1te.introgym.features.guide.presentation.exercises.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseCategoryItem
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseItem
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseCategoriesUseCase
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExercisesWithSelectedTagsUseCase
import ru.lonelywh1te.introgym.features.guide.domain.usecase.SearchExercisesByNameUseCase

class ExerciseCategoriesFragmentViewModel(
    private val getExerciseCategoriesUseCase: GetExerciseCategoriesUseCase,
    private val searchExercisesByNameUseCase: SearchExercisesByNameUseCase,
    private val getExercisesWithSelectedTagsUseCase: GetExercisesWithSelectedTagsUseCase,
): ViewModel() {
    private val _categories: MutableStateFlow<List<ExerciseCategoryItem>> = MutableStateFlow(emptyList())
    val categories: StateFlow<List<ExerciseCategoryItem>> get() = _categories

    private val _searchQuery: MutableStateFlow<String> = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery

    private val _selectedTagsIds: MutableStateFlow<List<Int>> = MutableStateFlow(emptyList())
    val selectedTagsIds: StateFlow<List<Int>> get() = _selectedTagsIds

    private val searchResults: MutableStateFlow<List<ExerciseItem>> = MutableStateFlow(emptyList())
    private val filterResults: MutableStateFlow<List<ExerciseItem>> = MutableStateFlow(emptyList())

    private val _exerciseList: Flow<List<ExerciseItem>> = combine(searchResults, filterResults) { searchResults, filterResults ->
        when {
            searchResults.isNotEmpty() && filterResults.isNotEmpty() -> {
                searchResults.filter { searchItem ->
                    filterResults.any { it.id == searchItem.id } }
            }
            searchResults.isNotEmpty() -> searchResults
            filterResults.isNotEmpty() -> filterResults
            else -> emptyList()
        }
    }
    val exerciseList: Flow<List<ExerciseItem>> get() = _exerciseList

    private val dispatcher = Dispatchers.IO

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSelectedTags(tagsIds: List<Int>) {
        _selectedTagsIds.value = tagsIds
    }

    private fun searchExercisesByName(searchQuery: String) {
        viewModelScope.launch (dispatcher) {
            searchExercisesByNameUseCase(searchQuery).collect { result ->
                searchResults.emit(result)
            }
        }
    }

    private fun filterExercises(selectedTagsIds: List<Int>) {
        viewModelScope.launch (dispatcher) {
            getExercisesWithSelectedTagsUseCase(selectedTagsIds).collect { result ->
                filterResults.emit(result)
            }
        }
    }

    init {
        viewModelScope.launch (dispatcher) {
            getExerciseCategoriesUseCase().collect {
                _categories.value = it
            }
        }

        viewModelScope.launch (dispatcher) {
            searchQuery.collect { query ->
                if (query.isNotEmpty()) {
                    searchExercisesByName(query)
                } else {
                    searchResults.emit(emptyList())
                }
            }
        }

        viewModelScope.launch (dispatcher) {
            selectedTagsIds.collect { tagsIds ->
                if (tagsIds.isNotEmpty()) {
                    filterExercises(tagsIds)
                } else {
                    filterResults.emit(emptyList())
                }
            }
        }

        viewModelScope.launch {
            _exerciseList.collect {
                searchResults.value.forEach {
                    Log.d("ExerciseCategoriesFragmentViewModel", "$it")
                }

                Log.d("ExerciseCategoriesFragmentViewModel", "________________________________________")

                filterResults.value.forEach {
                    Log.d("ExerciseCategoriesFragmentViewModel", "$it")
                }
            }
        }
    }
}