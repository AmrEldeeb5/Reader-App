package com.example.reader.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.utils.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserProfileViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _username = MutableStateFlow<String>("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    init {
        loadUsername()
    }

    private fun loadUsername() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    // Try to get username from Firestore first
                    val snapshot = firestore
                        .collection("users")
                        .document(currentUser.uid)
                        .get()
                        .await()

                    val firestoreUsername = if (snapshot.exists()) {
                        snapshot.getString("displayName")
                            ?: snapshot.getString("username")
                            ?: snapshot.getString("name")
                    } else null

                    // Fallback hierarchy: Firestore -> Firebase Auth -> UserPreferences -> Default
                    val resolvedUsername = firestoreUsername?.takeIf { it.isNotBlank() }
                        ?: currentUser.displayName?.takeIf { it.isNotBlank() }
                        ?: userPreferences.getSavedUserName()?.takeIf { it.isNotBlank() }
                        ?: "Andy"

                    _username.value = resolvedUsername
                } else {
                    // User not logged in, use saved username or default
                    _username.value = userPreferences.getSavedUserName()?.takeIf { it.isNotBlank() } ?: "Andy"
                }
            } catch (e: Exception) {
                // Fallback to saved username or default on error
                _username.value = userPreferences.getSavedUserName()?.takeIf { it.isNotBlank() } ?: "Andy"
            }
            _isLoading.value = false
        }
    }

    fun updateUsername(newUsername: String) {
        if (newUsername.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Update local state immediately for responsive UI
                _username.value = newUsername

                // Save to UserPreferences
                userPreferences.updateUserName(newUsername)

                // Update Firebase if user is logged in
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    // Update Firestore
                    firestore
                        .collection("users")
                        .document(currentUser.uid)
                        .update("displayName", newUsername)
                        .await()
                }
            } catch (e: Exception) {
                // On error, revert to previous username
                loadUsername()
            }
            _isLoading.value = false
        }
    }

    fun refreshUsername() {
        loadUsername()
    }
}
