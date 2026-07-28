package org.noxo.core.infra.org.noxo.core.infra.ble

import dev.bluefalcon.core.BlueFalcon
import dev.bluefalcon.peripheral.BluetoothAdvertiser
import org.noxo.core.common.PlatformContext

expect class FalconBluetooth(context: PlatformContext)
{
    val blueFalcon: BlueFalcon
    val advertiser: BluetoothAdvertiser
}