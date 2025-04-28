package ru.lonelywh1te.introgym.features.home.domain.usecase

import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.core.result.onSuccess
import ru.lonelywh1te.introgym.features.home.domain.repository.WorkoutLogRepository
import java.time.LocalDate

class GetWorkoutLogDatesUseCase(val repository: WorkoutLogRepository) {
    suspend operator fun invoke(week: List<LocalDate>): Result<List<LocalDate>> {
        return repository.getWorkoutLogDates()
            .onSuccess { Result.Success(it.filter { day -> day in week }) }
            .onFailure { Result.Failure(it) }
    }
}