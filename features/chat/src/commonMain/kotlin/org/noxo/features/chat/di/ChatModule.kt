package org.noxo.features.chat.di

import org.koin.dsl.module
import org.noxo.features.chat.ui.ChatViewModel

val chatModule = module {
    factory { (deviceId: String) -> ChatViewModel(deviceId, get()) }
}
