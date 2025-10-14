package com.example.reader.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.reader.utils.UserPreferences

@Stable
class AppThemeState(
    initialDark: Boolean,
    initialGreen: Boolean,
    private val prefs: UserPreferences
) {
    var isDarkTheme by mutableStateOf(initialDark)
        private set

    var isGreenTheme by mutableStateOf(initialGreen)
        private set

    fun setDark(value: Boolean) {
        if (isDarkTheme != value) {
            isDarkTheme = value
            // Persist immediately for consistency across app restarts
            prefs.setDarkTheme(value)
        }
    }

    fun setGreen(value: Boolean) {
        if (isGreenTheme != value) {
            isGreenTheme = value
            // Persist immediately for consistency across app restarts
            prefs.setGreenTheme(value)
        }
    }

    fun toggleDark() = setDark(!isDarkTheme)
    fun toggleGreen() = setGreen(!isGreenTheme)
}

@Composable
fun rememberAppThemeState(prefs: UserPreferences): AppThemeState {
    val systemDark = isSystemInDarkTheme()
    // Read saved preferences once; default dark follows system if null
    val initialDark = remember { prefs.getDarkTheme() ?: systemDark }
    val initialGreen = remember { prefs.getGreenTheme() }

    // Return a single stable state holder
    return remember { AppThemeState(initialDark, initialGreen, prefs) }
}
