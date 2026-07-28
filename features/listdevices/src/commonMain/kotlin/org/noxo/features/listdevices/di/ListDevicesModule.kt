package org.noxo.features.listdevices.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.noxo.features.listdevices.ui.ListDevicesViewModel

val listDevicesModule = module {
    factoryOf(::ListDevicesViewModel)
}