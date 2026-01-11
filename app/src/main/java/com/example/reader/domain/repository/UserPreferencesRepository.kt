package com.example.reader.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing user preferences.
 *
 * This interface defines the contract for user preference operations,
 * abstracting the underlying storage mechanism (SharedPreferences/EncryptedSharedPreferences).
 */
interface UserPreferencesRepository {
    
    /**
     * Save user credentials securely for "Remember Me" functionality.
     *
     * @param email User's email address
     * @param password User's password (stored encrypted)
     */
    suspend fun saveCredentials(email: String, password: String)
    
    /**
     * Clear saved credentials.
     */
    suspend fun clearCredentials()
    
    /**
     * Get the "Remember Me" preference.
     *
     * @return true if "Remember Me" is enabled, false otherwise
     */
    suspend fun getRememberMe(): Boolean
    
    /**
     * Set the "Remember Me" preference.
     *
     * @param rememberMe true to enable, false to disable
     */
    suspend fun setRememberMe(rememberMe: Boolean)
    
    /**
     * Get the dark theme preference.
     *
     * @return true for dark theme, false for light theme, null for system default
     */
    suspend fun getDarkTheme(): Boolean?
    
    /**
     * Set the dark theme preference.
     *
     * @param isDark true for dark theme, false for light theme
     */
    suspend fun setDarkTheme(isDark: Boolean)
    
    /**
     * Get the color scheme preference.
     *
     * @return true for green theme, false for brown theme
     */
    suspend fun getGreenTheme(): Boolean
    
    /**
     * Set the color scheme preference.
     *
     * @param isGreen true for green theme, false for brown theme
     */
    suspend fun setGreenTheme(isGreen: Boolean)
    
    /**
     * Observe the username.
     *
     * @return Flow emitting the current username or null
     */
    fun observeUsername(): Flow<String?>
    
    /**
     * Update the username.
     *
     * @param username New username
     */
    suspend fun updateUsername(username: String)

    /**
     * Get search history.
     *
     * @return List of recent search queries (max 10)
     */
    suspend fun getSearchHistory(): List<String>

    /**
     * Add a search query to history.
     *
     * @param query Search query to save
     */
    suspend fun addSearchHistory(query: String)

    /**
     * Clear all search history.
     */
    suspend fun clearSearchHistory()

    /**
     * Remove a specific search query from history.
     *
     * @param query Query to remove
     */
    suspend fun removeSearchHistory(query: String)

    /**
     * Get reading statistics.
     *
     * @return ReadingStats object with all statistics
     */
    suspend fun getReadingStats(): com.example.reader.domain.model.ReadingStats

    /**
     * Increment books read counter.
     */
    suspend fun incrementBooksRead()

    /**
     * Update reading streak.
     */
    suspend fun updateReadingStreak()

    /**
     * Add genre to favorites tracking.
     *
     * @param genre Genre name
     */
    suspend fun addGenreRead(genre: String)
}
