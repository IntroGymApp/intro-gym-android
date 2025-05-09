package ru.lonelywh1te.introgym.features.stats.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.core.result.onSuccess
import ru.lonelywh1te.introgym.features.stats.domain.StatsPeriod
import ru.lonelywh1te.introgym.features.stats.domain.usecase.GetTotalWeightStatsUseCase

class StatsFragmentViewModel(
    private val getTotalWeightStatsUseCase: GetTotalWeightStatsUseCase,
): ViewModel() {
    private val dispatcher = Dispatchers.IO

    private val _totalWeightPeriod: MutableStateFlow<StatsPeriod> = MutableStateFlow(StatsPeriod.Week())
    val totalWeightPeriod get() = _totalWeightPeriod.asStateFlow()

    val totalWeightEntries: StateFlow<List<BarEntry>> = _totalWeightPeriod
        .flatMapLatest { period ->
            var data: List<BarEntry> = emptyList()

            getTotalWeightStatsUseCase(period).map { result ->
                result
                    .onSuccess {
                        data = it.map { data ->
                            if (period is StatsPeriod.Year) {
                                BarEntry(data.key.monthValue.toFloat(), data.value)
                            } else {
                                BarEntry(data.key.dayOfMonth.toFloat(), data.value)
                            }
                        }
                    }
                    .onFailure {
                        _errors.emit(it)
                    }

                data
            }
        }
        .flowOn(dispatcher)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _errors: MutableSharedFlow<Error> = MutableSharedFlow()
    val errors: SharedFlow<Error> get() = _errors

    fun setTotalWeightPeriod(statsPeriod: StatsPeriod) {
        _totalWeightPeriod.value = statsPeriod
    }

    fun getAverageWeight(): Float {
        val average = totalWeightEntries.value.sumOf { it.y.toDouble() } / totalWeightEntries.value.size
        return average.toFloat()
    }
}