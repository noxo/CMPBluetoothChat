package org.noxo.core.domain.repository

import kotlinx.coroutines.flow.Flow
import org.noxo.core.domain.model.BluetoothDevice

interface BluetoothDeviceRepository {
    suspend fun startAdvertising(): Boolean
    suspend fun stopAdvertising()
    suspend fun scanForDevices(): Boolean
    fun discoveredDevices(): Flow<Set<BluetoothDevice>>
}