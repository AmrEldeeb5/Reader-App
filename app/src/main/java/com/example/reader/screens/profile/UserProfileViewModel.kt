package com.example.reader.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.data.realm.RealmRepository
import com.example.reader.utils.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserProfileViewModel(
    private val userPreferences: UserPreferences,
    private val realmRepository: RealmRepository
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

                    // Try to get from Realm DB
                    val realmProfile = realmRepository.getUserProfileOnce()
                    val realmUsername = realmProfile?.username?.takeIf { it.isNotBlank() }

                    // Fallback hierarchy: Firestore -> Realm DB -> Firebase Auth -> UserPreferences -> Default
                    val resolvedUsername = firestoreUsername?.takeIf { it.isNotBlank() }
                        ?: realmUsername
                        ?: currentUser.displayName?.takeIf { it.isNotBlank() }
                        ?: userPreferences.getSavedUserName()?.takeIf { it.isNotBlank() }
                        ?: "Andy"

                    _username.value = resolvedUsername
                } else {
                    // User not logged in, try Realm DB first, then saved username or default
                    val realmProfile = realmRepository.getUserProfileOnce()
                    _username.value = realmProfile?.username?.takeIf { it.isNotBlank() }
                        ?: userPreferences.getSavedUserName()?.takeIf { it.isNotBlank() }
                        ?: "Andy"
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

                // Save to Realm DB
                val email = auth.currentUser?.email ?: ""
                realmRepository.saveUserProfile(newUsername, email)

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
