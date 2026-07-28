package org.noxo.features.listdevices.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.noxo.core.domain.model.BluetoothDevice


@Composable
fun ListDevicesScreen(
    onDeviceClick: (String) -> Unit = {},
    listDevicesViewModel: ListDevicesViewModel = koinViewModel()
) {
    val state by listDevicesViewModel.uiState.collectAsStateWithLifecycle()

    ListDevicesContent(
        state = state,
        onDeviceClick = onDeviceClick
    )

    val failed = state.scanning == OperationState.Failed
            || state.advertising == OperationState.Failed

    if (failed) {
        FailedAlert(
            onRetryClick = { listDevicesViewModel.startScanning() }
        )
    }
}

@Composable
fun ListDevicesContent(
    state: ListDeviceUIState,
    onDeviceClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Text("Searching devices")
                        if (state.scanning == OperationState.Started) {
                            Spacer(modifier = Modifier.width(16.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyColumn {
                items(
                    items = state.devices.values.toList(),
                    key = { it.address }
                ) { device ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDeviceClick(device.address) }
                            .padding(16.dp)
                    ) {
                        Text(
                            text = device.name ?: "Unnamed Device",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = device.address,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FailedAlert(
    onRetryClick: () -> Unit
) {
    @OptIn(ExperimentalMaterial3Api::class)
    BasicAlertDialog(onDismissRequest = {}) {
        Surface(
            tonalElevation = 6.dp,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Failed start scan or advertise, see logs for error. Running on emulator or no permissions?",
                )
                Button(onClick = {  onRetryClick() }) {
                    Text(text = "Retry")
                }
            }
        }
    }
}

@Preview
@Composable
private fun ListDevicesPreview() {
    ListDevicesContent(
        state = ListDeviceUIState(
            scanning = OperationState.Started,
            devices = mapOf(
                "1" to BluetoothDevice("Pixel 8", "00:11:22:33:44:55"),
                "2" to BluetoothDevice(null, "AA:BB:CC:DD:EE:FF")
            )
        ),
        onDeviceClick = {}
    )
}
