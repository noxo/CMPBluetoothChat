package org.noxo.core.data.mapper

import dev.bluefalcon.core.BluetoothPeripheral
import org.noxo.core.domain.model.BluetoothDevice

fun BluetoothPeripheral.toDomain(): BluetoothDevice {
    return BluetoothDevice(
        name = this.name,
        address = this.uuid
    )
}