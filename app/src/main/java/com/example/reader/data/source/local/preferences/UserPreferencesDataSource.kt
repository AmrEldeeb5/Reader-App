package com.example.reader.data.source.local.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for user preferences using SharedPreferences and EncryptedSharedPreferences.
 *
 * This class manages both sensitive data (credentials) and non-sensitive data (theme, username).
 *
 * @property context Application context
 */
@Singleton
class UserPreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    // Encrypted SharedPreferences for sensitive data
    private val encryptedPrefs: SharedPreferences = try {
        EncryptedSharedPreferences.create(
            context,
            ENCRYPTED_PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        // Fallback to regular SharedPreferences if encrypted fails
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    // Regular SharedPreferences for non-sensitive data
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    // StateFlow for reactive username updates
    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()
    
    init {
        // Initialize username StateFlow
        _userName.value = prefs.getString(KEY_USER_NAME, null)
    }
    
    companion object {
        private const val PREFS_NAME = "reader_user_prefs"
        private const val ENCRYPTED_PREFS_NAME = "reader_secure_prefs"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_SAVED_EMAIL = "saved_email"
        private const val KEY_SAVED_PASSWORD = "saved_password"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_DARK_THEME = "dark_theme"
        private const val KEY_GREEN_THEME = "green_theme"
    }
    
    suspend fun saveCredentials(email: String, password: String) = withContext(Dispatchers.IO) {
        encryptedPrefs.edit {
            putString(KEY_SAVED_EMAIL, email)
            putString(KEY_SAVED_PASSWORD, password)
        }
    }
    
    suspend fun clearCredentials() = withContext(Dispatchers.IO) {
        encryptedPrefs.edit {
            remove(KEY_SAVED_EMAIL)
            remove(KEY_SAVED_PASSWORD)
        }
    }
    
    suspend fun getRememberMe(): Boolean = withContext(Dispatchers.IO) {
        prefs.getBoolean(KEY_REMEMBER_ME, false)
    }
    
    suspend fun setRememberMe(rememberMe: Boolean) = withContext(Dispatchers.IO) {
        prefs.edit {
            putBoolean(KEY_REMEMBER_ME, rememberMe)
        }
    }
    
    suspend fun getDarkTheme(): Boolean? = withContext(Dispatchers.IO) {
        if (prefs.contains(KEY_DARK_THEME)) {
            prefs.getBoolean(KEY_DARK_THEME, false)
        } else {
            null
        }
    }
    
    suspend fun setDarkTheme(isDark: Boolean) = withContext(Dispatchers.IO) {
        prefs.edit {
            putBoolean(KEY_DARK_THEME, isDark)
        }
    }
    
    suspend fun getGreenTheme(): Boolean = withContext(Dispatchers.IO) {
        prefs.getBoolean(KEY_GREEN_THEME, false)
    }
    
    suspend fun setGreenTheme(isGreen: Boolean) = withContext(Dispatchers.IO) {
        prefs.edit {
            putBoolean(KEY_GREEN_THEME, isGreen)
        }
    }
    
    fun observeUsername(): StateFlow<String?> = userName
    
    suspend fun updateUsername(username: String) = withContext(Dispatchers.IO) {
        prefs.edit {
            putString(KEY_USER_NAME, username)
        }
        _userName.value = username
    }
    
    suspend fun getSavedEmail(): String? = withContext(Dispatchers.IO) {
        encryptedPrefs.getString(KEY_SAVED_EMAIL, null)
    }
    
    suspend fun getSavedPassword(): String? = withContext(Dispatchers.IO) {
        encryptedPrefs.getString(KEY_SAVED_PASSWORD, null)
    }
}
