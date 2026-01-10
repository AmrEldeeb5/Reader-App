package com.example.reader.screens.SignUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.components.LoadingState
import com.example.reader.domain.error.AppError
import com.example.reader.domain.error.toUserFriendlyMessage
import com.example.reader.domain.repository.AuthRepository
import com.example.reader.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for sign up screen using Clean Architecture.
 *
 * Manages user registration and authentication state through repositories.
 *
 * @property authRepository Repository for authentication operations
 * @property userPreferencesRepository Repository for user preferences
 */
@HiltViewModel
class SignUpScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _signUpState = MutableStateFlow(LoadingState.IDLE)
    val signUpState: StateFlow<LoadingState> = _signUpState.asStateFlow()

    /**
     * Create a new user account with email and password.
     *
     * @param username Display name for the user
     * @param email User's email address
     * @param password User's password
     * @param onResult Callback with success status and optional error message
     */
    fun signUp(
        username: String,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) = viewModelScope.launch {
        if (_signUpState.value == LoadingState.LOADING) return@launch

        _loading.value = true
        _signUpState.value = LoadingState.LOADING

        val result = authRepository.signUp(email, password, username)
        
        result.fold(
            onSuccess = {
                _signUpState.value = LoadingState.SUCCESS
                _loading.value = false
                
                // Save username to preferences
                userPreferencesRepository.updateUsername(username)
                
                onResult(true, null)
            },
            onFailure = { error ->
                val message = error.toUserFriendlyMessage()

                _signUpState.value = LoadingState.error(message)
                _loading.value = false
                onResult(false, message)
            }
        )
    }

    /**
     * Reset the sign up state to idle.
     */
    fun resetState() {
        _signUpState.value = LoadingState.IDLE
        _loading.value = false
    }
}