package com.example.reader.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.domain.repository.AuthRepository
import com.example.reader.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for user profile screen using Clean Architecture.
 *
 * Manages username display and updates through repositories.
 *
 * @property authRepository Repository for authentication operations
 * @property userPreferencesRepository Repository for user preferences
 */
@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    /**
     * Reactive username from preferences with fallback to current user or default.
     */
    val username: StateFlow<String> = userPreferencesRepository
        .observeUsername()
        .map { savedUsername ->
            savedUsername?.takeIf { it.isNotBlank() }
                ?: authRepository.getCurrentUser()?.displayName?.takeIf { it.isNotBlank() }
                ?: "Andy"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Andy"
        )

    /**
     * Update the username.
     *
     * @param newUsername New username to set
     */
    fun updateUsername(newUsername: String) {
        if (newUsername.isBlank()) return

        viewModelScope.launch {
            userPreferencesRepository.updateUsername(newUsername)
        }
    }
}
