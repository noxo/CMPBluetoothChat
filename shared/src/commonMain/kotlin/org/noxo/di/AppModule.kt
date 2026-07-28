package org.noxo.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.noxo.core.infra.org.noxo.core.di.coreModule
import org.noxo.features.chat.di.chatModule
import org.noxo.features.listdevices.di.listDevicesModule

val appModule = module {
    includes(coreModule, listDevicesModule, chatModule)
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(appModule)
    }