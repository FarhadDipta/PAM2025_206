package com.example.spms.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    background = Color(0xFF0B1220),
    surface = Color(0xFF111827),
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun SPMSTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content
    )
}