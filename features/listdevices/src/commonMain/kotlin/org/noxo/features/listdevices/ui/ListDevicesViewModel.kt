package org.noxo.features.listdevices.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.noxo.core.domain.repository.BluetoothDeviceRepository

class ListDevicesViewModel(val bluetoothDeviceRepository: BluetoothDeviceRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ListDeviceUIState())
    val uiState: StateFlow<ListDeviceUIState> = bluetoothDeviceRepository.discoveredDevices()
        .combine(_uiState) { repoDevices, currentUiState ->
            currentUiState.copy(
                devices = repoDevices.associateBy { it.address }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ListDeviceUIState()
        )

    init {
        startAdvertising()
        startScanning()
    }

    fun startAdvertising() {
        viewModelScope.launch {
            _uiState.update { it.copy(advertising = OperationState.Started) }
            val success = bluetoothDeviceRepository.startAdvertising()
            if (!success) _uiState.update { it.copy(advertising = OperationState.Failed) }
        }
    }

    fun startScanning() {
        viewModelScope.launch {
            _uiState.update { it.copy(scanning = OperationState.Started) }
            val success = bluetoothDeviceRepository.scanForDevices()
            if (!success) _uiState.update { it.copy(scanning = OperationState.Failed) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            bluetoothDeviceRepository.stopAdvertising()
        }
    }
}