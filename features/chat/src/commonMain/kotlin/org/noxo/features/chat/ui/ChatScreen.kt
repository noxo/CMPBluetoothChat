package org.noxo.features.chat.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.noxo.core.domain.model.ConnectionStatus

@Composable
fun ChatScreen(
    deviceId: String,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = koinViewModel { parametersOf(deviceId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    ChatContent(
        deviceId = deviceId,
        uiState = uiState,
        onBackClick = onBackClick,
        onSendMessage = { viewModel.sendMessage(it) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatContent(
    deviceId: String,
    uiState: ChatUIState,
    onBackClick: () -> Unit,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Chat: $deviceId") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .imePadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") },
                    enabled = uiState.connectionStatus == ConnectionStatus.Connected
                )
                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            onSendMessage(textInput)
                            textInput = ""
                        }
                    },
                    enabled = textInput.isNotBlank() && uiState.connectionStatus == ConnectionStatus.Connected
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.connectionStatus != ConnectionStatus.Connected && !uiState.isLoading) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Status: ${uiState.connectionStatus}",
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                reverseLayout = true
            ) {
                items(uiState.messages.reversed()) { message ->
                    MessageItem(message)
                }
            }
        }
    }
}

@Composable
fun MessageItem(message: Message) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (message.isFromMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(8.dp),
                color = if (message.isFromMe) Color.White else Color.Black
            )
        }
    }
}

@Preview
@Composable
private fun ChatPreview() {
    MaterialTheme {
        ChatContent(
            deviceId = "Pixel 8",
            uiState = ChatUIState(
                messages = listOf(
                    Message("Hello!", false, 1000L),
                    Message("Hi there!", true, 2000L),
                    Message("How are you?", false, 3000L)
                ),
                connectionStatus = ConnectionStatus.Connected
            ),
            onBackClick = {},
            onSendMessage = {}
        )
    }
}
