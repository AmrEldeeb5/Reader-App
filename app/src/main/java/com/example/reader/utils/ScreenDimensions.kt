package com.example.reader.utils

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import android.app.Activity

/**
 * Responsive layout configuration based on Material Design 3 WindowSizeClass
 * This follows Google's recommended approach for adaptive layouts
 */
data class ResponsiveLayout(
    val widthClass: WindowWidthSizeClass,
    val heightClass: WindowHeightSizeClass,
    val imageHeight: Dp,
    val verticalSpacing: Dp,
    val horizontalPadding: Dp,
    val contentMaxWidth: Dp
) {
    // Computed properties for convenience
    val isCompact: Boolean get() = widthClass == WindowWidthSizeClass.Compact
    val isMedium: Boolean get() = widthClass == WindowWidthSizeClass.Medium
    val isExpanded: Boolean get() = widthClass == WindowWidthSizeClass.Expanded

    val isCompactHeight: Boolean get() = heightClass == WindowHeightSizeClass.Compact
    val isMediumHeight: Boolean get() = heightClass == WindowHeightSizeClass.Medium
    val isExpandedHeight: Boolean get() = heightClass == WindowHeightSizeClass.Expanded
}

/**
 * MODERN APPROACH: Calculate responsive layout using Material Design 3 WindowSizeClass
 *
 * Benefits:
 * - Follows Material Design 3 guidelines
 * - Handles phones, tablets, foldables, and desktop
 * - Uses official breakpoints (Compact: <600dp, Medium: 600-840dp, Expanded: >840dp)
 * - Better than manual threshold checking
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberResponsiveLayout(): ResponsiveLayout {
    val context = LocalContext.current
    val activity = context as? Activity

    // Calculate WindowSizeClass using Material3 API
    val windowSizeClass = if (activity != null) {
        calculateWindowSizeClass(activity)
    } else {
        // Fallback for preview/non-activity contexts
        val configuration = LocalConfiguration.current
        WindowSizeClass.calculateFromSize(
            DpSize(configuration.screenWidthDp.dp, configuration.screenHeightDp.dp)
        )
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    return remember(windowSizeClass, screenHeight) {
        val widthClass = windowSizeClass.widthSizeClass
        val heightClass = windowSizeClass.heightSizeClass

        // Adaptive image height based on both width and height classes
        val imageHeight = when {
            heightClass == WindowHeightSizeClass.Compact -> {
                // Very short screens (landscape phones)
                (screenHeight * 0.18f).dp.coerceIn(80.dp, 120.dp)
            }
            widthClass == WindowWidthSizeClass.Expanded -> {
                // Large screens (tablets, desktops)
                180.dp
            }
            widthClass == WindowWidthSizeClass.Medium -> {
                // Medium screens (large phones landscape, small tablets)
                (screenHeight * 0.22f).dp.coerceIn(140.dp, 200.dp)
            }
            else -> {
                // Compact width (phones portrait)
                when {
                    screenHeight < 520 -> (screenHeight * 0.20f).dp.coerceIn(100.dp, 140.dp)
                    screenHeight < 600 -> (screenHeight * 0.24f).dp.coerceIn(120.dp, 160.dp)
                    else -> (screenHeight * 0.28f).dp.coerceIn(140.dp, 220.dp)
                }
            }
        }

        // Adaptive vertical spacing
        val verticalSpacing = when (heightClass) {
            WindowHeightSizeClass.Compact -> 12.dp
            WindowHeightSizeClass.Medium -> 20.dp
            WindowHeightSizeClass.Expanded -> 32.dp
            else -> 16.dp
        }

        // Adaptive horizontal padding
        val horizontalPadding = when (widthClass) {
            WindowWidthSizeClass.Compact -> 20.dp
            WindowWidthSizeClass.Medium -> 32.dp
            WindowWidthSizeClass.Expanded -> 48.dp
            else -> 20.dp
        }

        // Content max width for large screens (prevents overly wide forms)
        val contentMaxWidth = when (widthClass) {
            WindowWidthSizeClass.Expanded -> 600.dp
            WindowWidthSizeClass.Medium -> 480.dp
            else -> Dp.Infinity
        }

        ResponsiveLayout(
            widthClass = widthClass,
            heightClass = heightClass,
            imageHeight = imageHeight,
            verticalSpacing = verticalSpacing,
            horizontalPadding = horizontalPadding,
            contentMaxWidth = contentMaxWidth
        )
    }
}
