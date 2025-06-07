package ru.lonelywh1te.introgym.app

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.lonelywh1te.introgym.app.activity.MainActivityViewModel

val appModule = module {
    viewModel<MainActivityViewModel> {
        MainActivityViewModel(
            launchPreferences = get(),
            errorDispatcher = get(),
        )
    }
}