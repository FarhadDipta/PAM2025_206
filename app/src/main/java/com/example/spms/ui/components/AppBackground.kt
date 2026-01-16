package com.example.spms.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun AppBackground(content: @Composable () -> Unit) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F172A), // dark navy
            Color(0xFF111827), // slate
            Color(0xFF0B1220)  // deeper
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        content()
    }
}