package org.noxo.core.data.repository

import kotlinx.coroutines.flow.Flow
import org.noxo.core.domain.model.BluetoothDevice
import org.noxo.core.domain.repository.BluetoothDeviceRepository
import org.noxo.core.infra.org.noxo.core.infra.ble.BluetoothDataSource

class BluetoothDeviceRepositoryImpl(
    private val dataSource: BluetoothDataSource
) : BluetoothDeviceRepository {
    override suspend fun startAdvertising(): Boolean = dataSource.startAdvertising()
    override suspend fun stopAdvertising() = dataSource.stopAdvertising()
    override suspend fun scanForDevices(): Boolean = dataSource.scanForDevices()
    override fun discoveredDevices(): Flow<Set<BluetoothDevice>> = dataSource.discoveredDevices()
}