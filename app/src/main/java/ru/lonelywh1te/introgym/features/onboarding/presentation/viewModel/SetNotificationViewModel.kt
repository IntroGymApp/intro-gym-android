package ru.lonelywh1te.introgym.features.onboarding.presentation.viewModel

import androidx.lifecycle.ViewModel
import ru.lonelywh1te.introgym.features.onboarding.domain.usecase.SetNotificationStateUseCase

class SetNotificationViewModel(
    private val setNotificationStateUseCase: SetNotificationStateUseCase,
): ViewModel() {

    fun setNotification(isEnabled: Boolean) {
        setNotificationStateUseCase(isEnabled)
    }

}