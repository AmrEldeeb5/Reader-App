package com.example.reader.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.domain.model.User
import com.example.reader.domain.repository.AuthRepository
import com.example.reader.domain.error.toUserFriendlyMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for authentication operations.
 *
 * Provides abstraction layer between UI and Firebase Auth,
 * following Clean Architecture principles.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _passwordChangeState = MutableStateFlow<PasswordChangeState>(PasswordChangeState.Idle)
    val passwordChangeState: StateFlow<PasswordChangeState> = _passwordChangeState.asStateFlow()

    init {
        checkAuthStatus()
    }

    /**
     * Check if user is currently authenticated.
     */
    private fun checkAuthStatus() {
        viewModelScope.launch {
            val isLoggedIn = authRepository.isUserLoggedIn()
            _authState.value = if (isLoggedIn) {
                authRepository.getCurrentUser().collect { user ->
                    _authState.value = if (user != null) {
                        AuthState.Authenticated(user)
                    } else {
                        AuthState.Unauthenticated
                    }
                }
                _authState.value
            } else {
                AuthState.Unauthenticated
            }
        }
    }

    /**
     * Get current user ID if authenticated.
     */
    fun getCurrentUserId(): String? = authRepository.getCurrentUserId()

    /**
     * Check if user is logged in.
     */
    fun isUserLoggedIn(): Boolean = authRepository.isUserLoggedIn()

    /**
     * Change user password with current password verification.
     *
     * @param currentPassword User's current password
     * @param newPassword New password to set
     */
    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _passwordChangeState.value = PasswordChangeState.Loading

            val result = authRepository.changePassword(currentPassword, newPassword)

            _passwordChangeState.value = result.fold(
                onSuccess = { PasswordChangeState.Success },
                onFailure = { error ->
                    val userFriendlyMessage = error.toUserFriendlyMessage()
                    PasswordChangeState.Error(userFriendlyMessage)
                }
            )
        }
    }

    /**
     * Sign out current user.
     */
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _authState.value = AuthState.Unauthenticated
        }
    }

    /**
     * Reset password change state to idle.
     */
    fun resetPasswordChangeState() {
        _passwordChangeState.value = PasswordChangeState.Idle
    }
}

/**
 * Sealed class representing authentication state.
 */
sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val user: User?) : AuthState()
    object Unauthenticated : AuthState()
}

/**
 * Sealed class representing password change operation state.
 */
sealed class PasswordChangeState {
    object Idle : PasswordChangeState()
    object Loading : PasswordChangeState()
    object Success : PasswordChangeState()
    data class Error(val message: String) : PasswordChangeState()
}

