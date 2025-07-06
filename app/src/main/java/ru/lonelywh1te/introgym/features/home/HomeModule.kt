package ru.lonelywh1te.introgym.features.home

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.lonelywh1te.introgym.features.home.data.WorkoutLogItemRepositoryImpl
import ru.lonelywh1te.introgym.features.home.domain.repository.WorkoutLogItemRepository
import ru.lonelywh1te.introgym.features.home.domain.usecase.AddWorkoutLogUseCase
import ru.lonelywh1te.introgym.features.home.domain.usecase.GetWorkoutLogDatesUseCase
import ru.lonelywh1te.introgym.features.home.domain.usecase.GetWorkoutLogItemListUseCase
import ru.lonelywh1te.introgym.features.home.presentation.viewModel.HomeFragmentViewModel

private val homeDataModule = module {
    single<WorkoutLogItemRepository> {
        WorkoutLogItemRepositoryImpl(
            workoutDao = get(),
            workoutLogDao = get(),
        )
    }
}

private val homeDomainModule = module {
    factory<GetWorkoutLogItemListUseCase> {
        GetWorkoutLogItemListUseCase(
            repository = get(),
        )
    }

    factory<AddWorkoutLogUseCase> {
        AddWorkoutLogUseCase(
            repository = get(),
        )
    }

    factory<GetWorkoutLogDatesUseCase> {
        GetWorkoutLogDatesUseCase(
            repository = get()
        )
    }
}

private val homePresentationModule = module {
    viewModel<HomeFragmentViewModel> {
        HomeFragmentViewModel(
            addWorkoutLogUseCase = get(),
            getWorkoutLogItemListUseCase = get(),
            getWorkoutLogDatesUseCase = get(),
            errorDispatcher = get(),
        )
    }
}

val homeModule = module {
    includes(homeDataModule, homeDomainModule, homePresentationModule)
}