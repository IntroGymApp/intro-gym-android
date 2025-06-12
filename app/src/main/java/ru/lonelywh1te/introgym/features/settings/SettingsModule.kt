package ru.lonelywh1te.introgym.features.settings

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.lonelywh1te.introgym.features.settings.presentation.SettingsFragmentViewModel

private val settingsDataModule = module {

}

private val settingsDomainModule = module {

}

private val settingsPresentationModule = module {
    viewModel<SettingsFragmentViewModel> {
        SettingsFragmentViewModel(
            settingsPreferences = get()
        )
    }
}

val settingsModule = module {
    includes(settingsDataModule, settingsDomainModule, settingsPresentationModule)
}