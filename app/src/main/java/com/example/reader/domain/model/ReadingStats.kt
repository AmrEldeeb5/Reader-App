package com.example.reader.domain.model

import androidx.compose.runtime.Immutable

/**
 * Reading statistics for user profile.
 */
@Immutable
data class ReadingStats(
    val totalBooksRead: Int = 0,
    val booksThisMonth: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val favoriteGenres: Map<String, Int> = emptyMap(),
    val lastReadDate: Long? = null,
    val totalFavorites: Int = 0
)

