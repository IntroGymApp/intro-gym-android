package ru.lonelywh1te.introgym.features.home

import org.koin.dsl.module

val homeDataModule = module {

}

val homeDomainModule = module {

}

val homePresentationModule = module {

}

val homeModule = module {
    includes(homeDataModule, homeDomainModule, homePresentationModule)
}