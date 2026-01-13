package com.example.reader.domain.model

import androidx.compose.runtime.Immutable

/**
 * Reading status for favorite books.
 */
enum class ReadingStatus {
    ALL,        // Default - just saved
    READING,    // Currently reading
    FINISHED    // Completed
}

/**
 * Domain model representing a favorited book with user-specific data.
 *
 * @property bookId Unique identifier for the book
 * @property book The book details
 * @property userRating User's personal rating for the book (optional)
 * @property addedTimestamp Timestamp when the book was added to favorites
 * @property readingStatus Current reading status of the book
 * @property currentPage Current page number (0 if not started)
 * @property totalPages Total pages in the book (0 if unknown)
 * @property progressPercentage Reading progress as percentage (0.0 to 100.0)
 * @property lastReadTimestamp Last time the book was read
 */
@Immutable
data class Favorite(
    val bookId: String,
    val book: Book,
    val userRating: Double?,
    val addedTimestamp: Long,
    val readingStatus: ReadingStatus = ReadingStatus.ALL,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val progressPercentage: Float = 0f,
    val lastReadTimestamp: Long = 0L
)
