package com.example.reader.screens.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.components.LoadingState
import com.example.reader.utils.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginScreenViewModel: ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _loginState = MutableStateFlow(LoadingState.IDLE)
    val loginState: StateFlow<LoadingState> = _loginState

    /**
     * Login with email and password using Firebase Authentication
     * Firebase automatically persists the session
     */
    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit)
    = viewModelScope.launch {
        if (_loginState.value == LoadingState.LOADING) return@launch
        _loginState.value = LoadingState.LOADING
        _loading.value = true

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _loading.value = false
                if (task.isSuccessful) {
                    _loginState.value = LoadingState.SUCCESS
                    onResult(true, null)
                } else {
                    _loginState.value = LoadingState.error(task.exception?.message ?: "Login failed")
                    onResult(false, task.exception?.message)
                }
            }
    }

    /**
     * Logout user from Firebase and clear saved preferences
     */
    fun logout(context: Context) {
        auth.signOut()
        val userPrefs = UserPreferences(context)
        userPrefs.clearAll()
    }

    /**
     * Check if user is already logged in (Firebase session active)
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}
