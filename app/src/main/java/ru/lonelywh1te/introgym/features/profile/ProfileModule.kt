package ru.lonelywh1te.introgym.features.profile

import org.koin.dsl.module

private val profileDataModule = module {

}

private val profileDomainModule = module {

}

private val profilePresentationModule = module {

}

val profileModule = module {
    includes(profileDataModule, profileDomainModule, profilePresentationModule)
}