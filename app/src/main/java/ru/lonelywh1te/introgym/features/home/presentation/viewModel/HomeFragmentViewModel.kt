package ru.lonelywh1te.introgym.features.home.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.core.result.onSuccess
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLog
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLogItem
import ru.lonelywh1te.introgym.features.home.domain.usecase.AddWorkoutLogUseCase
import ru.lonelywh1te.introgym.features.home.domain.usecase.GetWorkoutLogItemListUseCase
import java.time.LocalDate

class HomeFragmentViewModel(
    private val addWorkoutLogUseCase: AddWorkoutLogUseCase,
    private val getWorkoutLogItemListUseCase: GetWorkoutLogItemListUseCase,
): ViewModel() {
    private val dispatcher = Dispatchers.IO

    private val _workoutLogItems: MutableStateFlow<List<WorkoutLogItem>> = MutableStateFlow(emptyList())
    val workoutLogItems: StateFlow<List<WorkoutLogItem>> get() = _workoutLogItems

    private val _errors: MutableSharedFlow<Error> = MutableSharedFlow()
    val errors: SharedFlow<Error> get() = _errors

    fun addWorkoutLog(date: LocalDate, workoutId: Long) {
        viewModelScope.launch (dispatcher) {
            val workoutLog = WorkoutLog(
                workoutId = workoutId,
                date = date,
            )

            addWorkoutLogUseCase(workoutLog)
                .onSuccess { loadWorkoutLogItemList(date) }
                .onFailure { _errors.emit(it) }
        }
    }

    fun loadWorkoutLogItemList(date: LocalDate) {
        viewModelScope.launch (dispatcher) {
             getWorkoutLogItemListUseCase(date)
                 .onSuccess { _workoutLogItems.value = it }
                 .onFailure { _errors.emit(it) }
        }
    }
}