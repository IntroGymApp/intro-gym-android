package ru.lonelywh1te.introgym.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import ru.lonelywh1te.introgym.data.dataModule
import ru.lonelywh1te.introgym.features.auth.authModule
import ru.lonelywh1te.introgym.features.guide.guideModule
import ru.lonelywh1te.introgym.features.onboarding.onboardingModule

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(applicationContext)
            androidLogger(level = Level.DEBUG)
            modules(
                dataModule,
                authModule,
                guideModule,
                onboardingModule
            )
        }
    }
}