package org.noxo.core.domain.model

data class BluetoothDevice(
    val name: String?,
    val address: String,
    val rssi: Int? = null
)