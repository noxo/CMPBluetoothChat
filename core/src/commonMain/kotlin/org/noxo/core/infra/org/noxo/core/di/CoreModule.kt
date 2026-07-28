package org.noxo.core.infra.org.noxo.core.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.noxo.core.data.repository.BluetoothChatRepositoryImpl
import org.noxo.core.data.repository.BluetoothDeviceRepositoryImpl
import org.noxo.core.domain.repository.BluetoothChatRepository
import org.noxo.core.domain.repository.BluetoothDeviceRepository
import org.noxo.core.infra.org.noxo.core.infra.ble.BluetoothDataSource
import org.noxo.core.infra.org.noxo.core.infra.ble.FalconBluetooth
import org.noxo.core.infra.org.noxo.core.infra.ble.FalconBluetoothDataSource

val coreModule = module {
    singleOf(::FalconBluetooth)
    singleOf(::FalconBluetoothDataSource) bind BluetoothDataSource::class
    singleOf(::BluetoothDeviceRepositoryImpl) bind BluetoothDeviceRepository::class
    singleOf(::BluetoothChatRepositoryImpl) bind BluetoothChatRepository::class
}
