package org.noxo.features.chat.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.noxo.core.domain.model.ConnectionStatus
import org.noxo.core.domain.repository.BluetoothChatRepository

class ChatViewModel(
    private val deviceId: String,
    private val chatRepository: BluetoothChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUIState())
    val uiState: StateFlow<ChatUIState> = _uiState.asStateFlow()
    init {
        connect()
        observeMessages()
        observeConnectionStatus()
    }

    fun connect() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                chatRepository.connect(deviceId)
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun observeMessages() {
        chatRepository.observeMessages()
            .onEach { text ->
                val newMessage = Message(text = text, isFromMe = false)
                _uiState.update { it.copy(messages = it.messages + newMessage) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeConnectionStatus() {
        chatRepository.observeConnectionStatus()
            .onEach { status ->
                _uiState.update { it.copy(connectionStatus = status, isLoading = status == ConnectionStatus.Connecting) }
            }
            .launchIn(viewModelScope)
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            val success = chatRepository.sendMessage(text)
            if (success) {
                val newMessage = Message(text = text, isFromMe = true)
                _uiState.update { it.copy(messages = it.messages + newMessage) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            chatRepository.disconnect()
        }
    }
}
