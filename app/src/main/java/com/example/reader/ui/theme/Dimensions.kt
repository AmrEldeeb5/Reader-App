package com.example.reader.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Design system spacing constants.
 *
 * Use these instead of hardcoded dp values for consistent spacing throughout the app.
 */
object Spacing {
    /** Extra small spacing: 4dp - For tight spacing between related elements */
    val xs: Dp = 4.dp

    /** Small spacing: 8dp - For spacing between closely related items */
    val sm: Dp = 8.dp

    /** Medium spacing: 16dp - Default spacing for most UI elements */
    val md: Dp = 16.dp

    /** Large spacing: 24dp - For section separators */
    val lg: Dp = 24.dp

    /** Extra large spacing: 32dp - For major section breaks */
    val xl: Dp = 32.dp

    /** 2X Extra large spacing: 48dp - For hero sections */
    val xxl: Dp = 48.dp

    /** 3X Extra large spacing: 64dp - For splash/onboarding screens */
    val xxxl: Dp = 64.dp
}

/**
 * Design system elevation constants.
 *
 * Use these for consistent elevation/shadow depth.
 */
object Elevation {
    /** No elevation */
    val none: Dp = 0.dp

    /** Small elevation: 2dp - For cards at rest */
    val small: Dp = 2.dp

    /** Medium elevation: 4dp - For floating action buttons */
    val medium: Dp = 4.dp

    /** Large elevation: 8dp - For navigation drawer */
    val large: Dp = 8.dp

    /** Extra large elevation: 16dp - For dialogs */
    val xl: Dp = 16.dp
}

/**
 * Design system icon sizes.
 *
 * Use these for consistent icon sizing.
 */
object IconSize {
    /** Small icon: 16dp - For inline icons */
    val small: Dp = 16.dp

    /** Medium icon: 24dp - Standard icon size */
    val medium: Dp = 24.dp

    /** Large icon: 32dp - For prominent actions */
    val large: Dp = 32.dp

    /** Extra large icon: 48dp - For hero sections */
    val xl: Dp = 48.dp

    /** 2X Extra large icon: 64dp - For empty states */
    val xxl: Dp = 64.dp
}

/**
 * Design system corner radius values.
 *
 * Use these with RoundedCornerShape for consistent corner radii.
 */
object CornerRadius {
    /** Extra small radius: 4dp */
    val xs: Dp = 4.dp

    /** Small radius: 8dp - For small cards/chips */
    val sm: Dp = 8.dp

    /** Medium radius: 12dp - Standard cards */
    val md: Dp = 12.dp

    /** Large radius: 16dp - Large cards/sheets */
    val lg: Dp = 16.dp

    /** Extra large radius: 24dp - Hero cards */
    val xl: Dp = 24.dp

    /** Full circle */
    val full: Dp = 9999.dp
}

/**
 * Animation duration constants in milliseconds.
 */
object AnimationDuration {
    /** Fast animation: 150ms - For micro-interactions */
    const val FAST = 150

    /** Normal animation: 300ms - Standard animation speed */
    const val NORMAL = 300

    /** Slow animation: 500ms - For emphasis */
    const val SLOW = 500

    /** Extra slow: 700ms - For complex transitions */
    const val EXTRA_SLOW = 700
}

