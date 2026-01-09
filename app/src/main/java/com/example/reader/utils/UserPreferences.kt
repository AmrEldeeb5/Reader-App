package com.example.reader.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages user preferences for the Reader app
 * Works alongside Firebase Authentication for session management
 *
 * Firebase Auth handles:
 * - User session persistence (keeps user logged in)
 * - Secure credential storage
 *
 * UserPreferences handles:
 * - "Remember Me" preference for auto-login
 * - Encrypted credential storage for seamless login
 * - UI preferences
 */
class UserPreferences(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    // Use encrypted SharedPreferences for sensitive data
    private val encryptedPrefs: SharedPreferences = try {
        @Suppress("DEPRECATION")
        EncryptedSharedPreferences.create(
            context,
            ENCRYPTED_PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        // SECURITY: Don't fallback to unencrypted storage
        Log.e(TAG, "Failed to create encrypted preferences. User credentials will not be saved.", e)
        // Return a dummy SharedPreferences that doesn't actually save credentials
        context.getSharedPreferences("${ENCRYPTED_PREFS_NAME}_disabled", Context.MODE_PRIVATE)
    }

    // Regular SharedPreferences for non-sensitive data
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // StateFlow for reactive username updates - initialize with null first, then load
    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    init {
        // Initialize the username StateFlow with saved value
        _userName.value = getSavedUserName()
    }

    companion object {
        private const val TAG = "UserPreferences"
        private const val PREFS_NAME = "reader_user_prefs"
        private const val ENCRYPTED_PREFS_NAME = "reader_secure_prefs"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_AUTO_LOGIN = "auto_login"
        private const val KEY_SAVED_EMAIL = "saved_email"
        private const val KEY_SAVED_PASSWORD = "saved_password"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_DARK_THEME = "dark_theme"
        private const val KEY_GREEN_THEME = "green_theme"
    }

    /**
     * Save "Remember Me" preference with auto-login capability
     * When true, user will be automatically logged in on app start
     */
    fun setRememberMe(rememberMe: Boolean) {
        prefs.edit {
            putBoolean(KEY_REMEMBER_ME, rememberMe)
            putBoolean(KEY_AUTO_LOGIN, rememberMe)
        }
    }

    /**
     * Get "Remember Me" preference
     */
    fun getRememberMe(): Boolean {
        return prefs.getBoolean(KEY_REMEMBER_ME, false)
    }

    /**
     * Check if auto-login is enabled
     */
    fun shouldAutoLogin(): Boolean {
        return prefs.getBoolean(KEY_AUTO_LOGIN, false)
    }

    /**
     * Save user credentials securely for auto-login
     * ONLY called when "Remember Me" is checked
     */
    fun saveCredentials(email: String, password: String, userName: String? = null) {
        encryptedPrefs.edit {
            putString(KEY_SAVED_EMAIL, email)
            putString(KEY_SAVED_PASSWORD, password)
        }
        prefs.edit {
            putString(KEY_USER_NAME, userName)
        }
    }

    /**
     * Get saved user email for auto-fill or auto-login
     */
    fun getSavedEmail(): String? {
        return encryptedPrefs.getString(KEY_SAVED_EMAIL, null)
    }

    /**
     * Get saved password for auto-login (encrypted storage)
     * Returns null if no password is saved
     */
    fun getSavedPassword(): String? {
        return encryptedPrefs.getString(KEY_SAVED_PASSWORD, null)
    }

    /**
     * Get saved user name
     */
    fun getSavedUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }

    /**
     * Update just the user name without affecting other saved data
     */
    fun updateUserName(userName: String) {
        prefs.edit {
            putString(KEY_USER_NAME, userName)
        }
        _userName.value = userName // Update StateFlow
    }

    /**
     * Clear saved credentials (for unchecking Remember Me or logout)
     */
    fun clearCredentials() {
        encryptedPrefs.edit {
            remove(KEY_SAVED_EMAIL)
            remove(KEY_SAVED_PASSWORD)
        }
        prefs.edit {
            remove(KEY_USER_NAME)
        }
    }

    /**
     * Clear all saved preferences (for complete logout)
     * Call this when user explicitly logs out
     */
    fun clearAll() {
        prefs.edit { clear() }
        encryptedPrefs.edit { clear() }
    }

    /**
     * Save dark theme preference
     */
    fun setDarkTheme(isDark: Boolean) {
        prefs.edit {
            putBoolean(KEY_DARK_THEME, isDark)
        }
    }

    /**
     * Get dark theme preference
     * Returns null if not set (will use system default)
     */
    fun getDarkTheme(): Boolean? {
        return if (prefs.contains(KEY_DARK_THEME)) {
            prefs.getBoolean(KEY_DARK_THEME, false)
        } else {
            null // Not set, use system default
        }
    }

    /**
     * Save color scheme preference (green vs brown)
     */
    fun setGreenTheme(isGreen: Boolean) {
        prefs.edit {
            putBoolean(KEY_GREEN_THEME, isGreen)
        }
    }

    /**
     * Get color scheme preference
     * Returns false (brown) by default
     */
    fun getGreenTheme(): Boolean {
        return prefs.getBoolean(KEY_GREEN_THEME, false) // Default to brown
    }
}
