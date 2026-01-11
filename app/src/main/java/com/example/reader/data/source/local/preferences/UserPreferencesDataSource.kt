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
        private const val KEY_SEARCH_HISTORY = "search_history"
        private const val MAX_SEARCH_HISTORY = 10

        // Reading stats keys
        private const val KEY_BOOKS_READ = "books_read_total"
        private const val KEY_BOOKS_THIS_MONTH = "books_this_month"
        private const val KEY_CURRENT_STREAK = "current_streak"
        private const val KEY_LONGEST_STREAK = "longest_streak"
        private const val KEY_LAST_READ_DATE = "last_read_date"
        private const val KEY_GENRES = "favorite_genres"
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

    /**
     * Get search history (max 10 recent queries).
     */
    suspend fun getSearchHistory(): List<String> = withContext(Dispatchers.IO) {
        val historyString = prefs.getString(KEY_SEARCH_HISTORY, "") ?: ""
        if (historyString.isEmpty()) {
            emptyList()
        } else {
            historyString.split("|").filter { it.isNotBlank() }
        }
    }

    /**
     * Add a search query to history.
     * Removes duplicates and limits to MAX_SEARCH_HISTORY items.
     */
    suspend fun addSearchHistory(query: String) = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext

        val currentHistory = getSearchHistory().toMutableList()

        // Remove if already exists (to move it to front)
        currentHistory.remove(query)

        // Add to front
        currentHistory.add(0, query)

        // Limit to MAX_SEARCH_HISTORY
        val limitedHistory = currentHistory.take(MAX_SEARCH_HISTORY)

        // Save as pipe-separated string
        val historyString = limitedHistory.joinToString("|")
        prefs.edit {
            putString(KEY_SEARCH_HISTORY, historyString)
        }
    }

    /**
     * Clear all search history.
     */
    suspend fun clearSearchHistory() = withContext(Dispatchers.IO) {
        prefs.edit {
            remove(KEY_SEARCH_HISTORY)
        }
    }

    /**
     * Remove a specific query from search history.
     */
    suspend fun removeSearchHistory(query: String) = withContext(Dispatchers.IO) {
        val currentHistory = getSearchHistory().toMutableList()
        currentHistory.remove(query)

        val historyString = currentHistory.joinToString("|")
        prefs.edit {
            putString(KEY_SEARCH_HISTORY, historyString)
        }
    }

    /**
     * Get reading statistics.
     */
    suspend fun getReadingStats(): com.example.reader.domain.model.ReadingStats = withContext(Dispatchers.IO) {
        val totalBooksRead = prefs.getInt(KEY_BOOKS_READ, 0)
        val booksThisMonth = prefs.getInt(KEY_BOOKS_THIS_MONTH, 0)
        val currentStreak = prefs.getInt(KEY_CURRENT_STREAK, 0)
        val longestStreak = prefs.getInt(KEY_LONGEST_STREAK, 0)
        val lastReadDate = prefs.getLong(KEY_LAST_READ_DATE, 0).takeIf { it > 0 }

        // Parse genres
        val genresString = prefs.getString(KEY_GENRES, "") ?: ""
        val genres = if (genresString.isEmpty()) {
            emptyMap()
        } else {
            genresString.split("|").associate {
                val parts = it.split(":")
                parts[0] to (parts.getOrNull(1)?.toIntOrNull() ?: 0)
            }
        }

        com.example.reader.domain.model.ReadingStats(
            totalBooksRead = totalBooksRead,
            booksThisMonth = booksThisMonth,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            favoriteGenres = genres,
            lastReadDate = lastReadDate,
            totalFavorites = 0 // Will be calculated from favorites
        )
    }

    /**
     * Increment books read counter.
     */
    suspend fun incrementBooksRead() = withContext(Dispatchers.IO) {
        val current = prefs.getInt(KEY_BOOKS_READ, 0)
        val currentMonth = prefs.getInt(KEY_BOOKS_THIS_MONTH, 0)

        prefs.edit {
            putInt(KEY_BOOKS_READ, current + 1)
            putInt(KEY_BOOKS_THIS_MONTH, currentMonth + 1)
        }
    }

    /**
     * Update reading streak based on last read date.
     */
    suspend fun updateReadingStreak() = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val lastRead = prefs.getLong(KEY_LAST_READ_DATE, 0)

        val dayInMillis = 24 * 60 * 60 * 1000L
        val daysSinceLastRead = if (lastRead > 0) {
            ((now - lastRead) / dayInMillis).toInt()
        } else {
            Int.MAX_VALUE
        }

        val currentStreak = prefs.getInt(KEY_CURRENT_STREAK, 0)
        val longestStreak = prefs.getInt(KEY_LONGEST_STREAK, 0)

        val newStreak = when {
            daysSinceLastRead == 0 -> currentStreak // Same day
            daysSinceLastRead == 1 -> currentStreak + 1 // Consecutive day
            else -> 1 // Streak broken, start new
        }

        prefs.edit {
            putInt(KEY_CURRENT_STREAK, newStreak)
            putInt(KEY_LONGEST_STREAK, maxOf(longestStreak, newStreak))
            putLong(KEY_LAST_READ_DATE, now)
        }
    }

    /**
     * Add genre to favorites tracking.
     */
    suspend fun addGenreRead(genre: String) = withContext(Dispatchers.IO) {
        val genresString = prefs.getString(KEY_GENRES, "") ?: ""
        val genres = if (genresString.isEmpty()) {
            mutableMapOf()
        } else {
            genresString.split("|").associate {
                val parts = it.split(":")
                parts[0] to (parts.getOrNull(1)?.toIntOrNull() ?: 0)
            }.toMutableMap()
        }

        genres[genre] = (genres[genre] ?: 0) + 1

        val newGenresString = genres.entries.joinToString("|") { "${it.key}:${it.value}" }
        prefs.edit {
            putString(KEY_GENRES, newGenresString)
        }
    }
}
