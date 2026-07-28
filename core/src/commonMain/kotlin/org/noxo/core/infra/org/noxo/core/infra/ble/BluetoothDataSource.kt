package org.noxo.core.infra.org.noxo.core.infra.ble

import kotlinx.coroutines.flow.Flow
import org.noxo.core.domain.model.BluetoothDevice
import org.noxo.core.domain.model.ConnectionStatus

interface BluetoothDataSource {
    suspend fun startAdvertising() : Boolean
    suspend fun stopAdvertising()
    suspend fun scanForDevices() : Boolean
    fun discoveredDevices(): Flow<Set<BluetoothDevice>>
    suspend fun connect(address: String)
    suspend fun disconnect()
    suspend fun sendMessage(message: String): Boolean
    fun observeMessages(): Flow<String>
    fun observeConnectionStatus(): Flow<ConnectionStatus>
}
