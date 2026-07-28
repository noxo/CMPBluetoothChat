package org.noxo

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun App() {
    MaterialTheme {
        Scaffold(
            contentWindowInsets = WindowInsets.systemBars
        ) { innerPadding ->
            Navigation(modifier = Modifier.padding(innerPadding))
        }
    }
}


