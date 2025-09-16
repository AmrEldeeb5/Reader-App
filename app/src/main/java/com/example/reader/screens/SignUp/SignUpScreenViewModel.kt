package com.example.reader.screens.SignUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.components.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase as KtxFirebase
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
                if (!task.isSuccessful) {
                    _loading.value = false
                    val msg = task.exception?.message ?: "Sign up failed"
                    _signUpState.value = LoadingState.error(msg)
                    onResult(false, msg)
                } else {
                    val currentUser = auth.currentUser
                    currentUser?.updateProfile(
                        userProfileChangeRequest { displayName = user }
                    )?.addOnCompleteListener { updateTask ->
                        if (!updateTask.isSuccessful) {
                            _loading.value = false
                            val msg = updateTask.exception?.message ?: "Profile update failed"
                            _signUpState.value = LoadingState.error(msg)
                            onResult(false, msg)
                        } else {
                            // Now persist Firestore user doc
                            persistUserDoc(user) { persistOk, persistMsg ->
                                _loading.value = false
                                if (persistOk) {
                                    _signUpState.value = LoadingState.SUCCESS
                                    onResult(true, null)
                                } else {
                                    val pMsg = persistMsg ?: "User record save failed"
                                    _signUpState.value = LoadingState.error(pMsg)
                                    onResult(false, pMsg)
                                }
                            }
                        }
                    } ?: run {
                        // No profile update needed; still persist user
                        persistUserDoc(user) { persistOk, persistMsg ->
                            _loading.value = false
                            if (persistOk) {
                                _signUpState.value = LoadingState.SUCCESS
                                onResult(true, null)
                            } else {
                                val pMsg = persistMsg ?: "User record save failed"
                                _signUpState.value = LoadingState.error(pMsg)
                                onResult(false, pMsg)
                            }
                        }
                    }
                }
            }
    }

    private fun persistUserDoc(displayName: String?, callback: (Boolean, String?) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            callback(false, "Missing user id")
            return
        }
        val userMap = mapOf(
            "userId" to uid,
            "name" to (displayName ?: ""),
            "email" to (auth.currentUser?.email ?: "")
        )
        KtxFirebase.firestore
            .collection("users")
            .document(uid)
            .set(userMap, SetOptions.merge())
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }
}