package ru.lonelywh1te.introgym.core.di

import org.koin.dsl.module
import ru.lonelywh1te.introgym.core.db.MainDatabase

val databaseModule = module {

    single<MainDatabase> {
        MainDatabase.instance(context = get())
    }

}