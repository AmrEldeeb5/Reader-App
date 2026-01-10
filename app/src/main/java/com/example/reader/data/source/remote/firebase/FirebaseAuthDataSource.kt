package com.example.reader.data.source.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for Firebase Authentication operations.
 *
 * This class wraps Firebase Auth SDK and converts callbacks to coroutines/flows.
 *
 * @property firebaseAuth Firebase Authentication instance
 */
@Singleton
class FirebaseAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    
    /**
     * Sign in with email and password.
     *
     * @param email User's email address
     * @param password User's password
     * @return FirebaseUser on success
     * @throws Exception on failure
     */
    suspend fun signInWithEmail(email: String, password: String): FirebaseUser {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        return result.user ?: throw IllegalStateException("User is null after sign in")
    }
    
    /**
     * Sign up with email, password, and display name.
     *
     * @param email User's email address
     * @param password User's password
     * @param displayName User's display name
     * @return FirebaseUser on success
     * @throws Exception on failure
     */
    suspend fun signUpWithEmail(email: String, password: String, displayName: String): FirebaseUser {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw IllegalStateException("User is null after sign up")
        
        // Update display name
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()
        user.updateProfile(profileUpdates).await()
        
        return user
    }
    
    /**
     * Sign out the current user.
     */
    fun signOut() {
        firebaseAuth.signOut()
    }
    
    /**
     * Send password reset email.
     *
     * @param email User's email address
     * @throws Exception on failure
     */
    suspend fun sendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }
    
    /**
     * Change the current user's password.
     *
     * @param currentPassword User's current password for re-authentication
     * @param newPassword New password to set
     * @throws Exception on failure
     */
    suspend fun changePassword(currentPassword: String, newPassword: String) {
        val user = firebaseAuth.currentUser ?: throw IllegalStateException("No authenticated user")
        val email = user.email ?: throw IllegalStateException("User email not available")

        // Re-authenticate user with current password
        val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential).await()

        // Update password
        user.updatePassword(newPassword).await()
    }

    /**
     * Get the current user's ID.
     *
     * @return User ID if authenticated, null otherwise
     */
    fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

    /**
     * Observe the current authenticated user.
     *
     * @return Flow emitting FirebaseUser or null when auth state changes
     */
    fun observeCurrentUser(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(listener)
        
        // Send initial value
        trySend(firebaseAuth.currentUser)
        
        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }
    
    /**
     * Check if a user is currently logged in.
     *
     * @return true if authenticated, false otherwise
     */
    fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null
    
    /**
     * Get the current user synchronously.
     *
     * @return FirebaseUser or null
     */
    fun getCurrentUserSync(): FirebaseUser? = firebaseAuth.currentUser
}
