package org.noxo

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import org.noxo.features.chat.ui.ChatScreen
import org.noxo.features.listdevices.ui.ListDevicesScreen
import org.noxo.navigation.Chat
import org.noxo.navigation.ListDevices
import org.noxo.navigation.Route
import org.noxo.navigation.navSerializationConfig

@Composable
fun Navigation(modifier: Modifier) {
    val backStack = rememberNavBackStack(navSerializationConfig, ListDevices as Route)
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<ListDevices> {
                ListDevicesScreen(
                    onDeviceClick = { deviceId ->
                        backStack.add(Chat(deviceId))
                    }
                )
            }
            entry<Chat> { key ->
                ChatScreen(
                    deviceId = key.deviceId,
                    onBackClick = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}