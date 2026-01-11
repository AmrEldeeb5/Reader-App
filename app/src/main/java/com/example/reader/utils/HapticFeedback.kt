package com.example.reader.utils

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

/**
 * Utility for providing haptic feedback in Compose.
 */
class HapticFeedback(private val view: View) {

    /**
     * Light tap feedback for regular button presses.
     */
    fun lightTap() {
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    }

    /**
     * Success feedback for positive actions (favorite added, etc).
     */
    fun success() {
        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    }

    /**
     * Error/warning feedback for negative actions (delete, remove favorite).
     */
    fun warning() {
        view.performHapticFeedback(HapticFeedbackConstants.REJECT)
    }

    /**
     * Long press feedback.
     */
    fun longPress() {
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }

    /**
     * Swipe/drag feedback.
     */
    fun swipe() {
        view.performHapticFeedback(HapticFeedbackConstants.GESTURE_START)
    }
}

/**
 * Remember haptic feedback instance for the current view.
 */
@Composable
fun rememberHapticFeedback(): HapticFeedback {
    val view = LocalView.current
    return remember(view) { HapticFeedback(view) }
}

