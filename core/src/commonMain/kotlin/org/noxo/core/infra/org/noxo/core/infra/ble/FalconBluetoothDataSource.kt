package org.noxo.core.infra.org.noxo.core.infra.ble

import co.touchlab.kermit.Logger
import dev.bluefalcon.core.BluetoothCharacteristic
import dev.bluefalcon.core.BluetoothPeripheral
import dev.bluefalcon.core.BluetoothPeripheralState
import dev.bluefalcon.core.CharacteristicWriteResult
import dev.bluefalcon.core.CharacteristicWriteType
import dev.bluefalcon.core.ServiceDiscoveryPhase
import dev.bluefalcon.core.ServiceFilter
import dev.bluefalcon.peripheral.AdvertiseConfig
import dev.bluefalcon.peripheral.CharacteristicProperty
import dev.bluefalcon.peripheral.GattCharacteristicConfig
import dev.bluefalcon.peripheral.GattServiceConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.noxo.core.common.toUuid
import org.noxo.core.data.mapper.toDomain
import org.noxo.core.domain.model.BluetoothDevice
import org.noxo.core.domain.model.ConnectionStatus

class FalconBluetoothDataSource(val falconBluetooth: FalconBluetooth) : BluetoothDataSource {
    private val SERVICE_UUID = "0000d34d-0000-1000-8000-00805f9b34fb"
    private val CHARACTERISTIC_UUID = "0000d34d-0001-1000-8000-00805f9b34fb"
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _connectionStatus = MutableStateFlow(ConnectionStatus.Disconnected)
    private val _messages = MutableSharedFlow<String>()

    private var activePeripheral: BluetoothPeripheral? = null
    private var chatCharacteristic: BluetoothCharacteristic? = null

    private val gattCharacteristicConfig = GattCharacteristicConfig(
        uuid = CHARACTERISTIC_UUID,
        properties = setOf(
            CharacteristicProperty.WRITE_NO_RESPONSE,
            CharacteristicProperty.NOTIFY
        )
    )
    private val gattServiceConfig = GattServiceConfig(
        uuid = SERVICE_UUID, listOf(gattCharacteristicConfig)
    )
    private val advertiseConfig = AdvertiseConfig(
        localName = "",
        serviceUuids = listOf(SERVICE_UUID),
        services = listOf(gattServiceConfig)
    )

    init {

        falconBluetooth.blueFalcon.engine.characteristicNotifications
            .filter { it.characteristic.uuid == CHARACTERISTIC_UUID.toUuid() }
            .onEach { notification ->
                notification.value.decodeToString().let {
                    _messages.emit(it)
                }
            }
            .launchIn(scope)

        falconBluetooth.blueFalcon.connectionStateUpdates
            .onEach { update ->
                when (update.state) {
                    BluetoothPeripheralState.Connected -> {
                        _connectionStatus.value = ConnectionStatus.Connected
                        val state =
                            falconBluetooth.blueFalcon.connectionState(update.peripheral)
                        if (state == BluetoothPeripheralState.Connected) {
                            falconBluetooth.blueFalcon.discoverServices(update.peripheral)
                        }
                    }
                    BluetoothPeripheralState.Connecting -> _connectionStatus.value = ConnectionStatus.Connecting
                    BluetoothPeripheralState.Disconnected -> {
                        _connectionStatus.value = ConnectionStatus.Disconnected
                        activePeripheral = null
                        chatCharacteristic = null
                    }
                    else -> Unit
                }
            }
            .launchIn(scope)

        falconBluetooth.blueFalcon.serviceDiscoveryUpdates
            .onEach { update ->
                if (update.phase == ServiceDiscoveryPhase.ServicesDiscovered) {
                    update.peripheral.services
                        .firstOrNull { it.uuid == SERVICE_UUID.toUuid() }
                        ?.let { service ->
                            falconBluetooth.blueFalcon.discoverCharacteristics(update.peripheral, service)
                        }
                }
                if (update.phase == ServiceDiscoveryPhase.CharacteristicsDiscovered) {
                    update.peripheral.characteristics
                        .firstOrNull { it.uuid == CHARACTERISTIC_UUID.toUuid() }
                        ?.let { characteristic ->
                            chatCharacteristic = characteristic
                        }
                    activePeripheral = update.peripheral
                }
            }
            .launchIn(scope)

        falconBluetooth.advertiser.characteristicWriteRequests
            .filter { it.characteristicUuid.equals(CHARACTERISTIC_UUID, ignoreCase = true) }
            .onEach { request ->
                _messages.emit(request.value.decodeToString())
            }
            .launchIn(scope)
    }

    override suspend fun startAdvertising(): Boolean = withContext(Dispatchers.IO) {
        try {
            falconBluetooth.advertiser.startAdvertising(advertiseConfig)
            return@withContext true
        } catch (error: Exception) {
            Logger.w(error) { "startAdvertising failed" }
            return@withContext false
        }
    }

    override suspend fun stopAdvertising() = withContext(Dispatchers.IO) {
        falconBluetooth.advertiser.stopAdvertising()
    }

    override suspend fun scanForDevices(): Boolean = withContext(Dispatchers.IO) {
        try {
            falconBluetooth.blueFalcon.stopScanning()
            falconBluetooth.blueFalcon.clearPeripherals()
            falconBluetooth.blueFalcon.scan(listOf(ServiceFilter(SERVICE_UUID.toUuid())))
            return@withContext falconBluetooth.blueFalcon.isScanning
        } catch (error: Exception) {
            Logger.w(error) { "startScanning failed" }
            return@withContext false
        }
    }

    override fun discoveredDevices(): Flow<Set<BluetoothDevice>> =
        falconBluetooth.blueFalcon.peripherals.map { peripherals ->
            peripherals.map { it.toDomain() }.toSet()
        }

    override suspend fun connect(address: String) = withContext(Dispatchers.IO) {
        val peripheral = falconBluetooth.blueFalcon.peripherals.value.find { it.uuid == address }
        if (peripheral != null) {
            activePeripheral = peripheral
            falconBluetooth.blueFalcon.connect(peripheral)
        } else {
            Logger.w { "Peripheral with address $address not found in discovered devices" }
            _connectionStatus.value = ConnectionStatus.Error
        }
    }

    override suspend fun disconnect() = withContext(Dispatchers.IO) {
        activePeripheral?.let {
            falconBluetooth.blueFalcon.disconnect(it)
        }
        falconBluetooth.advertiser.stopAdvertising()
    }

    override suspend fun sendMessage(message: String): Boolean = withContext(Dispatchers.IO) {
        activePeripheral?.let { peripheral ->
            chatCharacteristic?.let { characteristic ->
                try {
                    val ret = falconBluetooth.blueFalcon.writeCharacteristic(peripheral, characteristic, message.encodeToByteArray(),
                        CharacteristicWriteType.WithoutResponse)
                    return@withContext ret == CharacteristicWriteResult.Sent
                } catch (e: Exception) {
                    Logger.w(e) { "Write failed" }
                }
            }
        }
        return@withContext false
    }

    override fun observeMessages(): Flow<String> = _messages.asSharedFlow()

    override fun observeConnectionStatus(): Flow<ConnectionStatus> = _connectionStatus.asStateFlow()
}
