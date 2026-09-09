package org.noxo.features.chat.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.noxo.core.domain.model.ConnectionStatus
import org.noxo.features.chat.resources.Res
import org.noxo.features.chat.resources.back
import org.noxo.features.chat.resources.chat_title
import org.noxo.features.chat.resources.connection_status
import org.noxo.features.chat.resources.message_placeholder
import org.noxo.features.chat.resources.send
import org.noxo.features.chat.resources.status_connected
import org.noxo.features.chat.resources.status_connecting
import org.noxo.features.chat.resources.status_disconnected
import org.noxo.features.chat.resources.status_error

@Composable
fun ChatScreen(
    deviceId: String,
    onBackClick: () -> Unit = {},
    showBackButton: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = koinViewModel { parametersOf(deviceId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    ChatContent(
        deviceId = deviceId,
        uiState = uiState,
        onBackClick = onBackClick,
        showBackButton = showBackButton,
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
    showBackButton: Boolean = true,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = { Text(stringResource(Res.string.chat_title, deviceId)) },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }},
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
                    placeholder = { Text(stringResource(Res.string.message_placeholder)) },
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
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = stringResource(Res.string.send))
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
                        text = stringResource(Res.string.connection_status, uiState.connectionStatus.label()),
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
private fun ConnectionStatus.label(): String = stringResource(
    when (this) {
        ConnectionStatus.Disconnected -> Res.string.status_disconnected
        ConnectionStatus.Connecting -> Res.string.status_connecting
        ConnectionStatus.Connected -> Res.string.status_connected
        ConnectionStatus.Error -> Res.string.status_error
    }
)

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
            showBackButton = true,
            onSendMessage = {}
        )
    }
}

private val sampleChatUIState = ChatUIState(
    messages = listOf(
        Message("Hello!", false, 1000L),
        Message("Hi there!", true, 2000L),
        Message("How are you?", false, 3000L)
    ),
    connectionStatus = ConnectionStatus.Connected
)

@Preview(widthDp = 411, heightDp = 731, name = "Phone")
@Composable
private fun ChatPreviewPhone() {
    MaterialTheme {
        ChatContent(
            deviceId = "Pixel 8",
            uiState = sampleChatUIState,
            onBackClick = {},
            showBackButton = true,
            onSendMessage = {}
        )
    }
}

@Preview(widthDp = 673, heightDp = 411, name = "Foldable")
@Composable
private fun ChatPreviewFoldable() {
    MaterialTheme {
        ChatContent(
            deviceId = "Foldable",
            uiState = sampleChatUIState,
            onBackClick = {},
            showBackButton = true,
            onSendMessage = {}
        )
    }
}

@Preview(widthDp = 1456, heightDp = 900, name = "Tablet")
@Composable
private fun ChatPreviewTablet() {
    MaterialTheme {
        ChatContent(
            deviceId = "Tablet",
            uiState = sampleChatUIState,
            onBackClick = {},
            showBackButton = false,
            onSendMessage = {}
        )
    }
}

@Preview(widthDp = 1600, heightDp = 900, name = "Desktop")
@Composable
private fun ChatPreviewDesktop() {
    MaterialTheme {
        ChatContent(
            deviceId = "Desktop",
            uiState = sampleChatUIState,
            onBackClick = {},
            showBackButton = false,
            onSendMessage = {}
        )
    }
}
