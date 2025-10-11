package com.example.reader.ui.theme

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for managing app theme preferences
 * Handles both dark/light mode and color scheme (green/brown)
 */
class ThemeViewModel : ViewModel() {
    private val _isGreenTheme = MutableStateFlow(true) // Green is default
    val isGreenTheme: StateFlow<Boolean> = _isGreenTheme.asStateFlow()

    fun toggleColorScheme() {
        _isGreenTheme.value = !_isGreenTheme.value
    }

    fun setColorScheme(isGreen: Boolean) {
        _isGreenTheme.value = isGreen
    }
}

