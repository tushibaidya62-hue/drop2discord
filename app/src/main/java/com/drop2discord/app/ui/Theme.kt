package com.drop2discord.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

private val AppColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF5865F2),
    onPrimary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    background = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    surface = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    onBackground = androidx.compose.ui.graphics.Color(0xFF000000),
    onSurface = androidx.compose.ui.graphics.Color(0xFF000000)
)

@Composable
fun Drop2DiscordTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        content = {
            Surface(modifier = Modifier.fillMaxSize()) {
                content()
            }
        }
    )
}
