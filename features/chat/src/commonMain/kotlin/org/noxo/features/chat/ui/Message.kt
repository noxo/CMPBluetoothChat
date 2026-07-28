package org.noxo.features.chat.ui

import kotlin.time.Clock

data class Message(
    val text: String,
    val isFromMe: Boolean,
    val timestamp: Long = Clock.System.now().epochSeconds
)
