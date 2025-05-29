package ru.lonelywh1te.introgym.features.notifications

import org.koin.dsl.module

val notificationModule = module {

    single<NotificationChannelManager> {
        NotificationChannelManager(get())
    }

}