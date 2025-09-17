package com.example.reader.screens.SignUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.components.LoadingState
import com.example.reader.data.MUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase as KtxFirebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignUpScreenViewModel: ViewModel() {

    companion object {
        private const val DEFAULT_QUOTE = "Live and let live"
        private const val DEFAULT_PROFESSION = "Reader"
        private const val USERS_COLLECTION = "users"

        // Error messages
        private const val ERROR_NO_USER = "No authenticated user found"
        private const val ERROR_MISSING_ID = "Missing user ID"
        private const val ERROR_SIGN_UP_FAILED = "Sign up failed"
    }
    private val auth: FirebaseAuth = Firebase.auth

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _signUpState = MutableStateFlow(LoadingState.IDLE)
    val signUpState: StateFlow<LoadingState> = _signUpState

    fun signUp(
        user: String,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) = viewModelScope.launch {
        if (_signUpState.value == LoadingState.LOADING) return@launch

        setLoadingState(true, LoadingState.LOADING)

        // Authoritative step: create account. Fail fast with friendly message
        val createError = runCatching { createUserAccount(email, password) }.exceptionOrNull()
        if (createError != null) {
            val msg = toUserMessage(createError)
            setLoadingState(false, LoadingState.error(msg))
            onResult(false, msg)
            return@launch
        }

        // Best-effort: don’t block success
        runCatching { updateUserProfile(user) }
        runCatching { saveUserToFirestore(user) }

        setLoadingState(false, LoadingState.SUCCESS)
        onResult(true, null)
    }

    private fun toUserMessage(e: Throwable): String = when (e) {
        is FirebaseAuthUserCollisionException -> "Account already exists. Please log in."
        is FirebaseAuthWeakPasswordException -> "Weak password. Use at least 6 characters."
        is FirebaseAuthInvalidCredentialsException -> "Invalid email format."
        else -> e.message ?: ERROR_SIGN_UP_FAILED
    }

    private suspend fun createUserAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    private suspend fun updateUserProfile(displayName: String) {
        val currentUser = auth.currentUser ?: throw Exception(ERROR_NO_USER)

        val profileUpdates = userProfileChangeRequest {
            this.displayName = displayName
        }

        currentUser.updateProfile(profileUpdates).await()
    }

    private suspend fun saveUserToFirestore(displayName: String) {
        val userId = auth.currentUser?.uid ?: throw Exception(ERROR_MISSING_ID)

        val userMap = createUserMap(userId, displayName)

        KtxFirebase.firestore
            .collection(USERS_COLLECTION)
            .document(userId)
            .set(userMap, SetOptions.merge())
            .await()
    }

    private fun createUserMap(userId: String, displayName: String) = MUser(
        userId = userId,
        displayName = displayName,
        avatarUrl = "",
        quote = DEFAULT_QUOTE,
        profession = DEFAULT_PROFESSION,
        id = null
    ).toMap()

    private fun setLoadingState(isLoading: Boolean, state: LoadingState = LoadingState.IDLE) {
        _loading.value = isLoading
        _signUpState.value = state
    }

}