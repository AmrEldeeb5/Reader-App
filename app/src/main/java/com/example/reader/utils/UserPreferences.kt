package com.example.reader.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Manages user preferences for the Reader app
 * Works alongside Firebase Authentication for session management
 *
 * Firebase Auth handles:
 * - User session persistence (keeps user logged in)
 * - Secure credential storage
 *
 * UserPreferences handles:
 * - "Remember Me" preference for email auto-fill
 * - UI preferences
 */
class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "reader_user_prefs"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_SAVED_EMAIL = "saved_email"
    }

    /**
     * Save "Remember Me" preference
     * When true, user's email will be auto-filled on login screen
     */
    fun setRememberMe(rememberMe: Boolean) {
        prefs.edit {
            putBoolean(KEY_REMEMBER_ME, rememberMe)
        }
    }

    /**
     * Get "Remember Me" preference
     */
    fun getRememberMe(): Boolean {
        return prefs.getBoolean(KEY_REMEMBER_ME, false)
    }

    /**
     * Save user email for auto-fill on login screen
     * Only used when "Remember Me" is checked
     */
    fun setSavedEmail(email: String?) {
        prefs.edit {
            if (email != null) {
                putString(KEY_SAVED_EMAIL, email)
            } else {
                remove(KEY_SAVED_EMAIL)
            }
        }
    }

    /**
     * Get saved user email for auto-fill
     * Returns null if no email is saved
     */
    fun getSavedEmail(): String? {
        return prefs.getString(KEY_SAVED_EMAIL, null)
    }

    /**
     * Clear all saved preferences (for logout)
     * Call this when user explicitly logs out
     */
    fun clearAll() {
        prefs.edit {
            clear()
        }
    }
}
