package ru.lonelywh1te.introgym.features.guide

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.lonelywh1te.introgym.features.guide.data.ExerciseCategoryRepositoryImpl
import ru.lonelywh1te.introgym.features.guide.domain.repository.ExerciseCategoryRepository
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseCategoriesUseCase
import ru.lonelywh1te.introgym.features.guide.presentation.viewModel.ExerciseCategoriesViewModel

val guideDataModule = module {
    single<ExerciseCategoryRepository> {
        ExerciseCategoryRepositoryImpl(exerciseCategoryDao = get())
    }
}

val guideDomainModule = module {
    factory<GetExerciseCategoriesUseCase> {
        GetExerciseCategoriesUseCase(repository = get())
    }
}

val guidePresentationModule = module {
    viewModel<ExerciseCategoriesViewModel> {
        ExerciseCategoriesViewModel(getExerciseCategoriesUseCase = get())
    }
}

val guideModule = module {
    includes(guideDataModule, guideDomainModule, guidePresentationModule)
}