package com.example.reader.domain.repository

import com.example.reader.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations.
 *
 * This interface defines the contract for authentication-related operations,
 * abstracting the underlying authentication provider (Firebase Auth).
 */
interface AuthRepository {
    
    /**
     * Sign in a user with email and password.
     *
     * @param email User's email address
     * @param password User's password
     * @return Result containing the authenticated User on success, or an exception on failure
     */
    suspend fun signIn(email: String, password: String): Result<User>
    
    /**
     * Sign up a new user with email, password, and display name.
     *
     * @param email User's email address
     * @param password User's password
     * @param displayName User's display name
     * @return Result containing the newly created User on success, or an exception on failure
     */
    suspend fun signUp(email: String, password: String, displayName: String): Result<User>
    
    /**
     * Sign out the current user.
     *
     * @return Result indicating success or failure
     */
    suspend fun signOut(): Result<Unit>
    
    /**
     * Send a password reset email to the specified email address.
     *
     * @param email User's email address
     * @return Result indicating success or failure
     */
    suspend fun resetPassword(email: String): Result<Unit>
    
    /**
     * Change the current user's password.
     *
     * @param currentPassword User's current password for verification
     * @param newPassword New password to set
     * @return Result indicating success or failure
     */
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit>

    /**
     * Get the current authenticated user ID.
     *
     * @return User ID if authenticated, null otherwise
     */
    fun getCurrentUserId(): String?

    /**
     * Observe the current authenticated user.
     *
     * @return Flow emitting the current User or null if not authenticated
     */
    fun getCurrentUser(): Flow<User?>
    
    /**
     * Check if a user is currently logged in.
     *
     * @return true if a user is authenticated, false otherwise
     */
    fun isUserLoggedIn(): Boolean
}
