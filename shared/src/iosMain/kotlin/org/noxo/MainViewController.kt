package org.noxo

import androidx.compose.ui.window.ComposeUIViewController
import org.noxo.core.infra.org.noxo.core.infra.ble.FalconBluetooth
import org.noxo.di.initKoin
import org.koin.dsl.module
import org.noxo.core.common.IOSPlatformContext
import org.noxo.core.common.PlatformContext

private val koinInitialization = lazy {
    initKoin {
        modules(module {
            single<PlatformContext> { IOSPlatformContext() }
            single { FalconBluetooth(get()) }
        })
    }
}
fun MainViewController() = ComposeUIViewController {
    koinInitialization.value
    App()
}