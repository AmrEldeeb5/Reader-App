package com.example.reader.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing app theme preferences using Clean Architecture.
 *
 * Handles dark/light mode and color scheme (green/brown) with persistence.
 *
 * @property userPreferencesRepository Repository for user preferences
 */
@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    
    private val _isDarkTheme = MutableStateFlow<Boolean?>(null)
    val isDarkTheme: StateFlow<Boolean?> = _isDarkTheme.asStateFlow()
    
    private val _isGreenTheme = MutableStateFlow(false)
    val isGreenTheme: StateFlow<Boolean> = _isGreenTheme.asStateFlow()

    init {
        loadThemePreferences()
    }

    private fun loadThemePreferences() {
        viewModelScope.launch {
            _isDarkTheme.value = userPreferencesRepository.getDarkTheme()
            _isGreenTheme.value = userPreferencesRepository.getGreenTheme()
        }
    }

    /**
     * Toggle between dark and light theme.
     */
    fun toggleDarkTheme() {
        viewModelScope.launch {
            val newValue = !(_isDarkTheme.value ?: false)
            _isDarkTheme.value = newValue
            userPreferencesRepository.setDarkTheme(newValue)
        }
    }

    /**
     * Set dark theme preference.
     *
     * @param isDark true for dark theme, false for light theme
     */
    fun setDarkTheme(isDark: Boolean) {
        viewModelScope.launch {
            _isDarkTheme.value = isDark
            userPreferencesRepository.setDarkTheme(isDark)
        }
    }

    /**
     * Toggle between green and brown color scheme.
     */
    fun toggleColorScheme() {
        viewModelScope.launch {
            val newValue = !_isGreenTheme.value
            _isGreenTheme.value = newValue
            userPreferencesRepository.setGreenTheme(newValue)
        }
    }

    /**
     * Set color scheme preference.
     *
     * @param isGreen true for green theme, false for brown theme
     */
    fun setColorScheme(isGreen: Boolean) {
        viewModelScope.launch {
            _isGreenTheme.value = isGreen
            userPreferencesRepository.setGreenTheme(isGreen)
        }
    }
}
