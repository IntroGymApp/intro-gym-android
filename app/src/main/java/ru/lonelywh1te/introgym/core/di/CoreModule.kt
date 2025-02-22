package ru.lonelywh1te.introgym.core.di

import org.koin.dsl.module
import ru.lonelywh1te.introgym.core.db.MainDatabase

private const val SHARED_PREFERENCES_NAME = "intro_gym_shared_prefs"

val coreModule = module {
    single<MainDatabase> {
        MainDatabase.instance(context = get())
    }
}