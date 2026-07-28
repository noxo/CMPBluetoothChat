package org.noxo.core.data.repository

import kotlinx.coroutines.flow.Flow
import org.noxo.core.domain.model.ConnectionStatus
import org.noxo.core.domain.repository.BluetoothChatRepository
import org.noxo.core.infra.org.noxo.core.infra.ble.BluetoothDataSource

class BluetoothChatRepositoryImpl(
    private val dataSource: BluetoothDataSource
) : BluetoothChatRepository {
    override suspend fun connect(address: String) = dataSource.connect(address)
    override suspend fun disconnect() = dataSource.disconnect()
    override suspend fun sendMessage(message: String): Boolean = dataSource.sendMessage(message)
    override fun observeMessages(): Flow<String> = dataSource.observeMessages()
    override fun observeConnectionStatus(): Flow<ConnectionStatus> = dataSource.observeConnectionStatus()
}
