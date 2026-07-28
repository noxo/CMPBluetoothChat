package org.noxo.core.domain.repository

import kotlinx.coroutines.flow.Flow
import org.noxo.core.domain.model.ConnectionStatus

interface BluetoothChatRepository {
    suspend fun connect(address: String)
    suspend fun disconnect()
    suspend fun sendMessage(message: String): Boolean
    fun observeMessages(): Flow<String>
    fun observeConnectionStatus(): Flow<ConnectionStatus>
}
