package ru.lonelywh1te.introgym.features.home.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.core.result.ErrorDispatcher
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.core.result.toUIState
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLog
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLogItem
import ru.lonelywh1te.introgym.features.home.domain.usecase.AddWorkoutLogUseCase
import ru.lonelywh1te.introgym.features.home.domain.usecase.GetWorkoutLogDatesUseCase
import ru.lonelywh1te.introgym.features.home.domain.usecase.GetWorkoutLogItemListUseCase
import java.time.LocalDate
import java.util.UUID

class HomeFragmentViewModel(
    private val addWorkoutLogUseCase: AddWorkoutLogUseCase,
    private val getWorkoutLogItemListUseCase: GetWorkoutLogItemListUseCase,
    private val getWorkoutLogDatesUseCase: GetWorkoutLogDatesUseCase,
    private val errorDispatcher: ErrorDispatcher,
): ViewModel() {
    private val dispatcher = Dispatchers.IO

    private val selectedDate: MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now())
    private val currentWeek: MutableStateFlow<List<LocalDate>> = MutableStateFlow(listOf())

    val workoutLogItemsState: StateFlow<UIState<List<WorkoutLogItem>>?> = selectedDate
        .flatMapLatest { date ->
            getWorkoutLogItemListUseCase(date).map { it.toUIState() }
        }
        .flowOn(dispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val markedDaysState: StateFlow<UIState<List<LocalDate>>?> = combine(currentWeek, workoutLogItemsState) { week, _ -> week }
        .flatMapLatest { week ->
            Log.d("HomeFragmentVM", week.toString())
            flow { emit(getWorkoutLogDatesUseCase(week).toUIState()) }
        }
        .flowOn(dispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun addWorkoutLog(date: LocalDate, workoutId: UUID) {
        viewModelScope.launch (dispatcher) {
            val workoutLog = WorkoutLog(
                workoutId = workoutId,
                date = date,
            )

            addWorkoutLogUseCase(workoutLog)
                .onFailure { errorDispatcher.dispatch(it) }
        }
    }

    fun setSelectedDate(date: LocalDate) {
        viewModelScope.launch (dispatcher) {
            selectedDate.value = date
        }
    }

    fun setCurrentWeek(week: List<LocalDate>) {
        currentWeek.value = week
    }
}