package ru.lonelywh1te.introgym.app

import android.app.Application
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import ru.lonelywh1te.introgym.core.coreModule
import ru.lonelywh1te.introgym.data.dataModule
import ru.lonelywh1te.introgym.features.guide.guideModule
import ru.lonelywh1te.introgym.features.home.homeModule
import ru.lonelywh1te.introgym.features.notifications.NotificationChannelManager
import ru.lonelywh1te.introgym.features.notifications.notificationModule
import ru.lonelywh1te.introgym.features.onboarding.onboardingModule
import ru.lonelywh1te.introgym.features.profile.profileModule
import ru.lonelywh1te.introgym.features.stats.statsModule
import ru.lonelywh1te.introgym.features.workout.workoutModule

class App: Application() {
    private val notificationChannelManager by inject<NotificationChannelManager>()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(applicationContext)
            androidLogger(level = Level.DEBUG)
            modules(
                appModule,
                coreModule,
                dataModule,
                guideModule,
                onboardingModule,
                workoutModule,
                homeModule,
                notificationModule,
                statsModule,
                profileModule,
            )
        }

        notificationChannelManager.initChannels()
    }
}