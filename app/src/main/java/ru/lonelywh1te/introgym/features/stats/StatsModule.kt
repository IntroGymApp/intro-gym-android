package ru.lonelywh1te.introgym.features.stats

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.lonelywh1te.introgym.features.stats.data.StatsRepositoryImpl
import ru.lonelywh1te.introgym.features.stats.domain.repository.StatsRepository
import ru.lonelywh1te.introgym.features.stats.domain.usecase.GetMusclesStatsUseCase
import ru.lonelywh1te.introgym.features.stats.domain.usecase.GetTotalWeightStatsUseCase
import ru.lonelywh1te.introgym.features.stats.presentation.StatsFragmentViewModel

private val statsDataModule = module {
    single<StatsRepository> {
        StatsRepositoryImpl(
            exerciseSetDao = get(),
        )
    }
}

private val statsDomainModule = module {

    factory<GetTotalWeightStatsUseCase> {
        GetTotalWeightStatsUseCase(
            repository = get(),
        )
    }

    factory<GetMusclesStatsUseCase> {
        GetMusclesStatsUseCase(
            repository = get(),
            exerciseCategoriesUseCase = get(),
        )
    }
}

private val statsPresentationModule = module {

    viewModel<StatsFragmentViewModel> {
        StatsFragmentViewModel(
            getTotalWeightStatsUseCase = get(),
            getMusclesStatsUseCase = get(),
            errorDispatcher = get(),
        )
    }

}

val statsModule = module {
    includes(statsDataModule, statsDomainModule, statsPresentationModule)
}