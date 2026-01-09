package com.example.reader.domain.model

import androidx.compose.runtime.Immutable

/**
 * Domain model representing a favorited book with user-specific data.
 *
 * @property bookId Unique identifier for the book
 * @property book The book details
 * @property userRating User's personal rating for the book (optional)
 * @property addedTimestamp Timestamp when the book was added to favorites
 */
@Immutable
data class Favorite(
    val bookId: String,
    val book: Book,
    val userRating: Double?,
    val addedTimestamp: Long
)
