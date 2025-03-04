package ru.lonelywh1te.introgym.features.guide

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.lonelywh1te.introgym.features.guide.data.ExerciseCategoryRepositoryImpl
import ru.lonelywh1te.introgym.features.guide.data.ExerciseRepositoryImpl
import ru.lonelywh1te.introgym.features.guide.domain.repository.ExerciseCategoryRepository
import ru.lonelywh1te.introgym.features.guide.domain.repository.ExerciseRepository
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseCategoriesUseCase
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseListUseCase
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseUseCase
import ru.lonelywh1te.introgym.features.guide.domain.usecase.SearchExercisesByNameUseCase
import ru.lonelywh1te.introgym.features.guide.presentation.viewModel.ExerciseCategoriesFragmentViewModel
import ru.lonelywh1te.introgym.features.guide.presentation.viewModel.ExerciseFragmentViewModel
import ru.lonelywh1te.introgym.features.guide.presentation.viewModel.ExerciseListFragmentViewModel

val guideDataModule = module {
    single<ExerciseCategoryRepository> {
        ExerciseCategoryRepositoryImpl(exerciseCategoryDao = get())
    }

    single<ExerciseRepository> {
        ExerciseRepositoryImpl(exerciseDao = get())
    }
}

val guideDomainModule = module {
    factory<GetExerciseCategoriesUseCase> {
        GetExerciseCategoriesUseCase(repository = get())
    }

    factory<GetExerciseListUseCase> {
        GetExerciseListUseCase(repository = get())
    }

    factory<GetExerciseUseCase> {
        GetExerciseUseCase(repository = get())
    }

    factory<SearchExercisesByNameUseCase> {
        SearchExercisesByNameUseCase(repository = get())
    }
}

val guidePresentationModule = module {
    viewModel<ExerciseCategoriesFragmentViewModel> {
        ExerciseCategoriesFragmentViewModel(
            getExerciseCategoriesUseCase = get(),
            searchExercisesByNameUseCase = get(),
        )
    }

    viewModel<ExerciseListFragmentViewModel> {
        ExerciseListFragmentViewModel(getExerciseListUseCase = get())
    }

    viewModel<ExerciseFragmentViewModel> {
        ExerciseFragmentViewModel(getExerciseUseCase = get())
    }
}

val guideModule = module {
    includes(guideDataModule, guideDomainModule, guidePresentationModule)
}