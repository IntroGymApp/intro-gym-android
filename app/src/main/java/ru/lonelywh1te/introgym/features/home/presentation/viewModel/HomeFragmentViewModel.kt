package ru.lonelywh1te.introgym.features.home.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.BaseError
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.core.result.onSuccess
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLog
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLogItem
import ru.lonelywh1te.introgym.features.home.domain.usecase.AddWorkoutLogUseCase
import ru.lonelywh1te.introgym.features.home.domain.usecase.GetWorkoutLogDatesUseCase
import ru.lonelywh1te.introgym.features.home.domain.usecase.GetWorkoutLogItemListUseCase
import ru.lonelywh1te.introgym.features.workout.domain.usecase.workout.DeleteWorkoutUseCase
import java.time.DayOfWeek
import java.time.LocalDate

class HomeFragmentViewModel(
    private val addWorkoutLogUseCase: AddWorkoutLogUseCase,
    private val getWorkoutLogItemListUseCase: GetWorkoutLogItemListUseCase,
    private val getWorkoutLogDatesUseCase: GetWorkoutLogDatesUseCase,
    private val deleteWorkoutUseCase: DeleteWorkoutUseCase,
): ViewModel() {
    private val dispatcher = Dispatchers.IO

    private val selectedDate: MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now())

    val workoutLogItems: StateFlow<List<WorkoutLogItem>> = selectedDate
        .flatMapLatest { date ->
            getWorkoutLogItemListUseCase(date).map { result ->
                var list: List<WorkoutLogItem> = emptyList()

                result
                    .onSuccess {
                        list = it
                        updateMarkedDays(date)
                    }
                    .onFailure { _errors.emit(it) }

                list
            }
        }
        .flowOn(dispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _markedDays: MutableStateFlow<List<LocalDate>> = MutableStateFlow(emptyList())
    val markedDays get() = _markedDays.asStateFlow()

    private val _errors: MutableSharedFlow<BaseError> = MutableSharedFlow()
    val errors get() = _errors.asSharedFlow()

    fun addWorkoutLog(date: LocalDate, workoutId: Long) {
        viewModelScope.launch (dispatcher) {
            val workoutLog = WorkoutLog(
                workoutId = workoutId,
                date = date,
            )

            addWorkoutLogUseCase(workoutLog)
                .onFailure { _errors.emit(it) }
        }
    }

    fun setSelectedDate(date: LocalDate) {
        viewModelScope.launch (dispatcher) {
            selectedDate.value = date
        }
    }

    fun updateMarkedDays(week: List<LocalDate>) {
        viewModelScope.launch (dispatcher) {
            getWorkoutLogDatesUseCase(week)
                .onSuccess { _markedDays.value = it }
                .onFailure { _errors.emit(it) }
        }
    }

    fun deleteWorkoutLog(workoutId: Long) {
        viewModelScope.launch (dispatcher) {
            deleteWorkoutUseCase(workoutId)
                .onFailure { _errors.emit(it) }
        }
    }

    private fun updateMarkedDays(selectedDate: LocalDate) {
        viewModelScope.launch (dispatcher) {
            val week = List(7) { selectedDate.with(DayOfWeek.MONDAY).plusDays(it.toLong()) }

            getWorkoutLogDatesUseCase(week)
                .onSuccess { _markedDays.value = it }
                .onFailure { _errors.emit(it) }
        }
    }
}