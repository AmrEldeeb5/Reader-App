package com.example.reader.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.components.LoadingState
import com.example.reader.domain.error.AppError
import com.example.reader.domain.repository.AuthRepository
import com.example.reader.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for login screen using Clean Architecture.
 *
 * Manages authentication state and user login operations through repositories.
 *
 * @property authRepository Repository for authentication operations
 * @property userPreferencesRepository Repository for user preferences
 */
@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _loginState = MutableStateFlow(LoadingState.IDLE)
    val loginState: StateFlow<LoadingState> = _loginState.asStateFlow()

    /**
     * Attempt to sign in with email and password.
     *
     * @param email User's email address
     * @param password User's password
     * @param rememberMe Whether to save credentials for auto-login
     * @param onResult Callback with success status and optional error message
     */
    fun login(
        email: String,
        password: String,
        rememberMe: Boolean = false,
        onResult: (Boolean, String?) -> Unit
    ) = viewModelScope.launch {
        if (_loginState.value == LoadingState.LOADING) return@launch
        
        _loginState.value = LoadingState.LOADING
        _loading.value = true

        val result = authRepository.signIn(email, password)
        
        result.fold(
            onSuccess = {
                _loginState.value = LoadingState.SUCCESS
                _loading.value = false
                
                // Save credentials if "Remember Me" is checked
                if (rememberMe) {
                    userPreferencesRepository.saveCredentials(email, password)
                    userPreferencesRepository.setRememberMe(true)
                }
                
                onResult(true, null)
            },
            onFailure = { error ->
                val message = when (error) {
                    is AppError.AuthError -> error.message
                    is AppError.NetworkError -> "Network error. Please check your connection."
                    else -> "Login failed. Please try again."
                }
                
                _loginState.value = LoadingState.error(message)
                _loading.value = false
                onResult(false, message)
            }
        )
    }

    /**
     * Sign out the current user and clear saved preferences.
     */
    fun logout() = viewModelScope.launch {
        authRepository.signOut()
        userPreferencesRepository.clearCredentials()
        userPreferencesRepository.setRememberMe(false)
    }

    /**
     * Check if a user is currently logged in.
     *
     * @return true if user is logged in, false otherwise
     */
    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }
}
