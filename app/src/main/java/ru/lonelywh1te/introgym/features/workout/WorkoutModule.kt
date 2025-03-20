package ru.lonelywh1te.introgym.features.workout

import org.koin.dsl.module

val workoutDataModule = module {

}

val workoutDomainModule = module {

}

val workoutPresentationModule = module {

}

val workoutModule = module {
    includes(workoutDataModule, workoutDomainModule, workoutPresentationModule)
}