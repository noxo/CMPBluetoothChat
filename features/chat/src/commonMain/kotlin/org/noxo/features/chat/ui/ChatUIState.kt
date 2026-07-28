package org.noxo.features.chat.ui

import org.noxo.core.domain.model.ConnectionStatus

data class ChatUIState(
    val messages: List<Message> = emptyList(),
    val connectionStatus: ConnectionStatus = ConnectionStatus.Disconnected,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
