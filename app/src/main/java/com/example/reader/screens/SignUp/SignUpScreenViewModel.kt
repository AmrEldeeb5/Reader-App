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
            val errorMessage = e.message ?: "Sign up failed"
            setLoadingState(false, LoadingState.error(errorMessage))
            onResult(false, errorMessage)
        }
    }

    private suspend fun createUserAccount(email: String, password: String) {
        val task = auth.createUserWithEmailAndPassword(email, password).await()
        if (task.user == null) {
            throw Exception("Failed to create user account")
        }
    }

    private suspend fun updateUserProfile(displayName: String) {
        val currentUser = auth.currentUser ?: throw Exception("No authenticated user found")

        val profileUpdates = userProfileChangeRequest {
            this.displayName = displayName
        }

        currentUser.updateProfile(profileUpdates).await()
    }

    private suspend fun saveUserToFirestore(displayName: String) {
        val userId = auth.currentUser?.uid ?: throw Exception("Missing user ID")

        val userMap = MUser(
            userId = userId,
            displayName = displayName,
            avatarUrl = "",
            quote = "Live and let live",
            profession = "Reader",
            id = null
        ).toMap()

        KtxFirebase.firestore
            .collection("users")
            .document(userId)
            .set(userMap, SetOptions.merge())
            .await()
    }

    private fun setLoadingState(isLoading: Boolean, state: LoadingState = LoadingState.IDLE) {
        _loading.value = isLoading
        _signUpState.value = state
    }

    // Alternative callback-based version if you prefer not to use coroutines/await
    fun signUpWithCallbacks(
        user: String,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) = viewModelScope.launch {
        if (_signUpState.value == LoadingState.LOADING) return@launch

        setLoadingState(true)

        // Step 1: Create account
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { createTask ->
                if (!createTask.isSuccessful) {
                    handleError(createTask.exception, onResult)
                    return@addOnCompleteListener
                }

                // Step 2: Update profile
                updateProfileWithCallback(user) { profileSuccess, profileError ->
                    if (!profileSuccess) {
                        handleError(Exception(profileError), onResult)
                        return@updateProfileWithCallback
                    }

                    // Step 3: Save to Firestore
                    saveToFirestoreWithCallback(user) { firestoreSuccess, firestoreError ->
                        if (firestoreSuccess) {
                            setLoadingState(false, LoadingState.SUCCESS)
                            onResult(true, null)
                        } else {
                            handleError(Exception(firestoreError), onResult)
                        }
                    }
                }
            }
    }

    private fun updateProfileWithCallback(
        displayName: String,
        callback: (Boolean, String?) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, "No authenticated user found")
            return
        }

        val profileUpdates = userProfileChangeRequest {
            this.displayName = displayName
        }

        currentUser.updateProfile(profileUpdates)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    private fun saveToFirestoreWithCallback(
        displayName: String,
        callback: (Boolean, String?) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            callback(false, "Missing user ID")
            return
        }

        val userMap = MUser(
            userId = userId,
            displayName = displayName,
            avatarUrl = "",
            quote = "Live and let live",
            profession = "Reader",
            id = null
        ).toMap()

        KtxFirebase.firestore
            .collection("users")
            .document(userId)
            .set(userMap, SetOptions.merge())
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    private fun handleError(exception: Exception?, onResult: (Boolean, String?) -> Unit) {
        val errorMessage = exception?.message ?: "Sign up failed"
        setLoadingState(false, LoadingState.error(errorMessage))
        onResult(false, errorMessage)
    }

    // Keep the original persistUserDoc method for backward compatibility
    private fun persistUserDoc(displayName: String?, callback: (Boolean, String?) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            callback(false, "Missing user id")
            return
        }
        val userMap = MUser(
            userId = userId,
            displayName = displayName ?: "",
            avatarUrl = "",
            quote = "Live and let live",
            profession = "Reader",
            id = null).toMap()
        KtxFirebase.firestore
            .collection("users")
            .document(userId)
            .set(userMap, SetOptions.merge())
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }
}