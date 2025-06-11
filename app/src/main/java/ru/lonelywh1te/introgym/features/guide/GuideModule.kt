package ru.lonelywh1te.introgym.features.guide

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.lonelywh1te.introgym.features.guide.data.repository.ExerciseCategoryRepositoryImpl
import ru.lonelywh1te.introgym.features.guide.data.repository.ExerciseRepositoryImpl
import ru.lonelywh1te.introgym.features.guide.data.repository.TagRepositoryImpl
import ru.lonelywh1te.introgym.features.guide.domain.repository.ExerciseCategoryRepository
import ru.lonelywh1te.introgym.features.guide.domain.repository.ExerciseRepository
import ru.lonelywh1te.introgym.features.guide.domain.repository.TagRepository
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseCategoriesUseCase
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseListUseCase
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseTagsUseCase
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExerciseUseCase
import ru.lonelywh1te.introgym.features.guide.domain.usecase.GetExercisesWithSelectedTagsUseCase
import ru.lonelywh1te.introgym.features.guide.domain.usecase.SearchExercisesByNameUseCase
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.viewModel.ExerciseCategoriesFragmentViewModel
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.viewModel.ExerciseFilterFragmentViewModel
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.viewModel.ExerciseFragmentViewModel
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.viewModel.ExerciseListFragmentViewModel

val guideDataModule = module {
    single<ExerciseCategoryRepository> {
        ExerciseCategoryRepositoryImpl(exerciseCategoryDao = get())
    }

    single<ExerciseRepository> {
        ExerciseRepositoryImpl(exerciseDao = get())
    }

    single<TagRepository> {
        TagRepositoryImpl(tagDao = get())
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

    factory<GetExerciseTagsUseCase> {
        GetExerciseTagsUseCase(repository = get())
    }

    factory<GetExercisesWithSelectedTagsUseCase> {
        GetExercisesWithSelectedTagsUseCase(repository = get())
    }
}

val guidePresentationModule = module {
    viewModel<ExerciseCategoriesFragmentViewModel> {
        ExerciseCategoriesFragmentViewModel(
            getExerciseCategoriesUseCase = get(),
            searchExercisesByNameUseCase = get(),
            getExercisesWithSelectedTagsUseCase = get(),
        )
    }

    viewModel<ExerciseListFragmentViewModel> {
        ExerciseListFragmentViewModel(getExerciseListUseCase = get())
    }

    viewModel<ExerciseFragmentViewModel> {
        ExerciseFragmentViewModel(getExerciseUseCase = get())
    }

    viewModel<ExerciseFilterFragmentViewModel> {
        ExerciseFilterFragmentViewModel(getExerciseTagsUseCase = get())
    }
}

val guideModule = module {
    includes(guideDataModule, guideDomainModule, guidePresentationModule)
}