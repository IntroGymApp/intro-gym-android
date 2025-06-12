package ru.lonelywh1te.introgym.core

import org.koin.dsl.module
import ru.lonelywh1te.introgym.core.result.ErrorDispatcher
import ru.lonelywh1te.introgym.core.result.MainErrorDispatcher

val coreModule = module {

    single<ErrorDispatcher> {
        MainErrorDispatcher(
            context = get()
        )
    }

}