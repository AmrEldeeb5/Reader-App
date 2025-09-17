package com.example.reader.screens.SignUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.components.LoadingState
import com.example.reader.data.MUser
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
import kotlinx.coroutines.tasks.await

class SignUpScreenViewModel: ViewModel() {

    companion object {
        private const val DEFAULT_QUOTE = "Live and let live"
        private const val DEFAULT_PROFESSION = "Reader"
        private const val USERS_COLLECTION = "users"

        // Error messages
        private const val ERROR_CREATE_ACCOUNT = "Failed to create user account"
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
        // Prevent multiple simultaneous sign-up attempts
        if (_signUpState.value == LoadingState.LOADING) return@launch

        try {
            setLoadingState(true)

            // Step 1: Create user account with email and password
            createUserAccount(email, password)

            // Step 2: Update user profile with display name
            updateUserProfile(user)

            // Step 3: Save user data to Firestore
            saveUserToFirestore(user)

            // Success
            setLoadingState(false, LoadingState.SUCCESS)
            onResult(true, null)

        } catch (e: Exception) {
            val errorMessage = e.message ?: ERROR_SIGN_UP_FAILED
            setLoadingState(false, LoadingState.error(errorMessage))
            onResult(false, errorMessage)
        }
    }

    private suspend fun createUserAccount(email: String, password: String) {
        val task = auth.createUserWithEmailAndPassword(email, password).await()
        if (task.user == null) {
            throw Exception(ERROR_CREATE_ACCOUNT)
        }
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