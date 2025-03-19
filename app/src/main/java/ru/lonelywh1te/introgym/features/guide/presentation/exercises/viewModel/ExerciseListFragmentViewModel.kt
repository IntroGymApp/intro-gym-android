package ru.lonelywh1te.introgym.features.guide.presentation.exercises.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseItem
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseListUseCase

class ExerciseListFragmentViewModel(
    private val getExerciseListUseCase: GetExerciseListUseCase,
): ViewModel() {
    private val exerciseItemByCategory: MutableStateFlow<List<ExerciseItem>> = MutableStateFlow(emptyList())

    private val _searchQuery: MutableStateFlow<String> = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery

    private val _selectedTagsIds: MutableStateFlow<List<Int>> = MutableStateFlow(emptyList())
    val selectedTagsIds: StateFlow<List<Int>> get() = _selectedTagsIds

    private val searchResults: MutableStateFlow<List<ExerciseItem>> = MutableStateFlow(emptyList())
    private val filterResults: MutableStateFlow<List<ExerciseItem>> = MutableStateFlow(emptyList())

    private val _exerciseItems: Flow<List<ExerciseItem>> = combine(exerciseItemByCategory, searchResults, filterResults) { exerciseItemsByCategory , searchResults, filterResults ->
        when {
            _searchQuery.value.isNotEmpty() && _selectedTagsIds.value.isNotEmpty() -> {
                searchResults.filter { searchItem ->
                    filterResults.any { it.id == searchItem.id } }
            }
            _searchQuery.value.isNotEmpty() -> searchResults
            _selectedTagsIds.value.isNotEmpty() -> filterResults
            else -> exerciseItemsByCategory
        }
    }
    val exerciseItems: Flow<List<ExerciseItem>> = _exerciseItems

    private val dispatcher = Dispatchers.IO

    fun getExerciseItems(categoryId: Long) {
        viewModelScope.launch (dispatcher) {
            getExerciseListUseCase(categoryId).collect { list ->
                exerciseItemByCategory.emit(list)
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSelectedTags(tagsIds: List<Int>) {
        _selectedTagsIds.value = tagsIds
    }

    private fun searchExercisesByName(searchQuery: String) {
        val result = exerciseItemByCategory.value.filter { it.name.contains(searchQuery, true) }
        searchResults.value = result
    }

    private fun filterExercises(selectedTagsIds: List<Int>) {
        val result = exerciseItemByCategory.value.filter { it.tagsIds.containsAll(selectedTagsIds) }
        filterResults.value = result
    }

    init {
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
    }
}