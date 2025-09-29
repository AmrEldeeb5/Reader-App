package com.example.reader.screens.SignUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.components.LoadingState
import com.example.reader.data.model.MUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreException.Code
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase as KtxFirebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.util.Log

class SignUpScreenViewModel: ViewModel() {

    companion object {
        private const val DEFAULT_QUOTE = "Live and let live"
        private const val DEFAULT_PROFESSION = "Reader"
        private const val USERS_COLLECTION = "users"
        private const val TAG = "SignUpVM"

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

        // Try to create the account first; if it already exists, try signing in
        val uidOrError = runCatching { createUserAccount(email, password) }
        val uid: String? = if (uidOrError.isSuccess) {
            uidOrError.getOrNull()
        } else {
            val e = uidOrError.exceptionOrNull()
            when (e) {
                is FirebaseAuthUserCollisionException -> {
                    // Account exists — sign in instead
                    runCatching { signIn(email, password) }
                        .onFailure { signInErr ->
                            val msg = toUserMessage(signInErr)
                            setLoadingState(false, LoadingState.error(msg))
                            onResult(false, msg)
                        }
                        .getOrNull()
                }
                else -> {
                    val msg = toUserMessage(e ?: Exception("Unknown auth error"))
                    setLoadingState(false, LoadingState.error(msg))
                    onResult(false, msg)
                    null
                }
            }
        }
        if (uid == null) return@launch

        // Best-effort profile update
        runCatching { updateUserProfile(user) }
            .onFailure { Log.w(TAG, "Profile update failed", it) }

        // Try to save user doc; if permission denied, log but DO NOT block navigation
        val saveRes = runCatching { saveUserToFirestore(uid, user) }
        if (saveRes.isFailure) {
            val ex = saveRes.exceptionOrNull()
            val isPermissionDenied = (ex is FirebaseFirestoreException && ex.code == Code.PERMISSION_DENIED)
            if (isPermissionDenied) {
                Log.w(TAG, "Firestore permission denied while creating user doc; proceeding to Home", ex)
            } else {
                Log.e(TAG, "Failed to save user document to Firestore", ex)
            }
            // Proceed regardless to avoid trapping the user on Sign Up screen
        }

        setLoadingState(false, LoadingState.SUCCESS)
        onResult(true, null)
    }

    private fun toUserMessage(e: Throwable): String = when (e) {
        is FirebaseAuthUserCollisionException -> "Account already exists. Please log in."
        is FirebaseAuthWeakPasswordException -> "Weak password. Use at least 6 characters."
        is FirebaseAuthInvalidCredentialsException -> "Invalid email format."
        else -> e.message ?: ERROR_SIGN_UP_FAILED
    }

    // Return the UID from the created user to avoid timing issues with auth.currentUser
    private suspend fun createUserAccount(email: String, password: String): String {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw IllegalStateException(ERROR_MISSING_ID)
        return uid
    }

    private suspend fun updateUserProfile(displayName: String) {
        val currentUser = auth.currentUser ?: throw Exception(ERROR_NO_USER)
        val profileUpdates = userProfileChangeRequest { this.displayName = displayName }
        currentUser.updateProfile(profileUpdates).await()
    }

    private suspend fun saveUserToFirestore(userId: String, displayName: String) {
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

    private suspend fun signIn(email: String, password: String): String {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw IllegalStateException(ERROR_MISSING_ID)
        return uid
    }

}