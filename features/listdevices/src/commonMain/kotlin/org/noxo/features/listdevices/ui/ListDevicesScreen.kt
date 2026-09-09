package org.noxo.features.listdevices.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.noxo.core.domain.model.BluetoothDevice
import org.noxo.features.listdevices.resources.Res
import org.noxo.features.listdevices.resources.retry
import org.noxo.features.listdevices.resources.scan_failed
import org.noxo.features.listdevices.resources.searching_devices
import org.noxo.features.listdevices.resources.unnamed_device


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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDevicesContent(
    state: ListDeviceUIState,
    onDeviceClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Text(stringResource(Res.string.searching_devices))
                        if (state.scanning == OperationState.Started) {
                            Spacer(modifier = Modifier.width(16.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(240.dp),
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp)
        ) {
            items(
                items = state.devices.values.toList(),
                key = { it.address }
            ) { device ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDeviceClick(device.address) }
                ) {
                    ListDevice(device)
                }
            }
        }
    }
}

@Composable
private fun ListDevice(device: BluetoothDevice) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = device.name ?:  stringResource(Res.string.unnamed_device),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = device.address,
            style = MaterialTheme.typography.bodySmall
        )
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
                    text = stringResource(Res.string.scan_failed),
                )
                Button(onClick = {  onRetryClick() }) {
                    Text(text = stringResource(Res.string.retry))
                }
            }
        }
    }
}

private val sampleListDeviceUIState = ListDeviceUIState(
    scanning = OperationState.Started,
    devices = mapOf(
        "1" to BluetoothDevice("Pixel 8", "00:11:22:33:44:55"),
        "2" to BluetoothDevice(null, "AA:BB:CC:DD:EE:FF"),
        "3" to BluetoothDevice("Galaxy S24", "11:22:33:44:55:66"),
        "4" to BluetoothDevice("iPad Air", "22:33:44:55:66:77")
    )
)

@Preview
@Composable
private fun ListDevicesPreview() {
    MaterialTheme {
        ListDevicesContent(
            state = sampleListDeviceUIState,
            onDeviceClick = {}
        )
    }
}

@Preview(widthDp = 411, heightDp = 731, name = "Phone")
@Composable
private fun ListDevicesPreviewPhone() {
    MaterialTheme {
        ListDevicesContent(
            state = sampleListDeviceUIState,
            onDeviceClick = {}
        )
    }
}

@Preview(widthDp = 673, heightDp = 411, name = "Foldable")
@Composable
private fun ListDevicesPreviewFoldable() {
    MaterialTheme {
        ListDevicesContent(
            state = sampleListDeviceUIState,
            onDeviceClick = {}
        )
    }
}

@Preview(widthDp = 1456, heightDp = 900, name = "Tablet")
@Composable
private fun ListDevicesPreviewTablet() {
    MaterialTheme {
        ListDevicesContent(
            state = sampleListDeviceUIState,
            onDeviceClick = {}
        )
    }
}

@Preview(widthDp = 1600, heightDp = 900, name = "Desktop")
@Composable
private fun ListDevicesPreviewDesktop() {
    MaterialTheme {
        ListDevicesContent(
            state = sampleListDeviceUIState,
            onDeviceClick = {}
        )
    }
}
