package org.noxo

import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowWidthSizeClass
import org.noxo.features.chat.ui.ChatScreen
import org.noxo.features.listdevices.ui.ListDevicesScreen
import org.noxo.navigation.Chat
import org.noxo.navigation.ListDevices
import org.noxo.navigation.Route
import org.noxo.navigation.navSerializationConfig

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun Navigation(modifier: Modifier) {
    val backStack = rememberNavBackStack(navSerializationConfig, ListDevices as Route)
    val listDetailSceneStrategy = rememberListDetailSceneStrategy<NavKey>()
    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val showBackButton = windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        sceneStrategies = listOf(listDetailSceneStrategy),
        entryProvider = entryProvider {
            entry<ListDevices>(
                metadata = ListDetailSceneStrategy.listPane(detailPlaceholder = { Text("Select a device") })
            ) {
                ListDevicesScreen(
                    onDeviceClick = { deviceId ->
                        backStack.add(Chat(deviceId))
                    }
                )
            }
            entry<Chat>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) { key ->
                ChatScreen(
                    deviceId = key.deviceId,
                    showBackButton = showBackButton,
                    onBackClick = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}
