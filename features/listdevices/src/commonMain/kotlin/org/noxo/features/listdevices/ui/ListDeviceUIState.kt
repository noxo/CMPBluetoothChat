package org.noxo.features.listdevices.ui

import org.noxo.core.domain.model.BluetoothDevice

sealed class OperationState {
    object Started : OperationState()
    object Stopped : OperationState()
    object Failed : OperationState()
}

data class ListDeviceUIState(
    val scanning: OperationState = OperationState.Stopped,
    val advertising: OperationState = OperationState.Stopped,
    val devices: Map<String, BluetoothDevice> = emptyMap()
)