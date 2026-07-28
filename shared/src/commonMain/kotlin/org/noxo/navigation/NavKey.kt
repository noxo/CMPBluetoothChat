package org.noxo.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
sealed interface Route : NavKey

@Serializable
data object ListDevices : Route

@Serializable
data class Chat(val deviceId: String) : Route

val navSerializationConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(ListDevices::class, ListDevices.serializer())
            subclass(Chat::class, Chat.serializer())
        }
    }
}
