package ru.lonelywh1te.introgym.features.guide.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.data.db.TagType
import ru.lonelywh1te.introgym.features.guide.domain.model.Tag
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseTagsUseCase

class ExerciseFilterFragmentViewModel(
    private val getExerciseTagsUseCase: GetExerciseTagsUseCase,
): ViewModel() {
    private val _muscleTags: MutableStateFlow<List<Tag>> = MutableStateFlow(emptyList())
    private val _equipmentTags: MutableStateFlow<List<Tag>> = MutableStateFlow(emptyList())
    private val _difficultyTags: MutableStateFlow<List<Tag>> = MutableStateFlow(emptyList())

    val muscleTags: StateFlow<List<Tag>> get() = _muscleTags
    val equipmentTags: StateFlow<List<Tag>> get() = _equipmentTags
    val difficultyTags: StateFlow<List<Tag>> get() = _difficultyTags

    private val dispatcher = Dispatchers.IO

    init {
        viewModelScope.launch (dispatcher) {
           getExerciseTagsUseCase().collect { list ->
               _muscleTags.emit(list.filter { it.type == TagType.MUSCLE })
               _equipmentTags.emit(list.filter { it.type == TagType.EQUIPMENT })
               _difficultyTags.emit(list.filter { it.type == TagType.DIFFICULTY })
           }
        }
    }
}