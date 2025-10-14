package com.example.reader.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme

@Composable
fun animatedScaffoldContainerColor(durationMillis: Int = 300, label: String = "scaffold_bg"): Color {
    val target = MaterialTheme.colorScheme.background
    val animated by animateColorAsState(targetValue = target, animationSpec = tween(durationMillis), label = label)
    return animated
}

@Composable
fun animatedTopBarContainerColor(durationMillis: Int = 300, label: String = "topbar_bg"): Color {
    val target = MaterialTheme.colorScheme.background
    val animated by animateColorAsState(targetValue = target, animationSpec = tween(durationMillis), label = label)
    return animated
}

@Composable
fun animatedOnBackgroundColor(durationMillis: Int = 300, label: String = "on_bg"): Color {
    val target = MaterialTheme.colorScheme.onBackground
    val animated by animateColorAsState(targetValue = target, animationSpec = tween(durationMillis), label = label)
    return animated
}

@Composable
fun animatedOnSurfaceVariantColor(durationMillis: Int = 300, label: String = "on_surface_variant"): Color {
    val target = MaterialTheme.colorScheme.onSurfaceVariant
    val animated by animateColorAsState(targetValue = target, animationSpec = tween(durationMillis), label = label)
    return animated
}
