package org.noxo.core.infra.org.noxo.core.infra.ble

import dev.bluefalcon.core.BlueFalcon
import dev.bluefalcon.peripheral.BluetoothAdvertiser
import dev.bluefalcon.peripheral.apple.createBluetoothAdvertiser
import dev.bluefalcon.plugins.logging.LogLevel
import dev.bluefalcon.plugins.logging.LoggingPlugin
import dev.bluefalcon.plugins.retry.RetryPlugin
import dev.bluefalcon.engine.ios.IosEngine
import org.noxo.core.common.PlatformContext

actual class FalconBluetooth actual constructor(context: PlatformContext) {
    private val engine = IosEngine()
    actual val advertiser: BluetoothAdvertiser = createBluetoothAdvertiser()

    actual val blueFalcon: BlueFalcon = BlueFalcon(
        engine = engine
    ).apply {
        plugins.install(LoggingPlugin(LoggingPlugin.Config().apply {
            level = LogLevel.DEBUG
            logDiscovery = true
            logConnections = true
            logGattOperations = true
        })) { }

        plugins.install(RetryPlugin(RetryPlugin.Config().apply {
            maxRetries = 3
            initialDelay = kotlin.time.Duration.parse("1s")
        })) { }

    }
}
