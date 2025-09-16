package com.example.reader.screens.SignUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.components.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpScreenViewModel: ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _signUpState = MutableStateFlow(LoadingState.IDLE)
    val signUpState: StateFlow<LoadingState> = _signUpState

    fun signUp(user: String, email: String, password: String, onResult: (Boolean, String?) -> Unit)
            = viewModelScope.launch {
        if (_signUpState.value == LoadingState.LOADING) return@launch
        _signUpState.value = LoadingState.LOADING
        _loading.value = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _loading.value = false
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    currentUser?.updateProfile(
                        userProfileChangeRequest { displayName = user }
                    )?.addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            _signUpState.value = LoadingState.SUCCESS
                            onResult(true, null)
                        } else {
                            val msg = updateTask.exception?.message ?: "Profile update failed"
                            _signUpState.value = LoadingState.error(msg)
                            onResult(false, msg)
                        }
                    } ?: run {
                        _signUpState.value = LoadingState.SUCCESS
                        onResult(true, null)
                    }
                } else {
                    val msg = task.exception?.message ?: "Sign up failed"
                    _signUpState.value = LoadingState.error(msg)
                    onResult(false, msg)
                }
            }
    }

    fun resetState() { _signUpState.value = LoadingState.IDLE }
}