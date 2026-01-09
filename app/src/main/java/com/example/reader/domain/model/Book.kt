package com.example.reader.domain.model

import androidx.compose.runtime.Immutable

/**
 * Domain model representing a book in the Reader application.
 *
 * @property id Unique identifier for the book
 * @property title Book title
 * @property author Book author(s)
 * @property subtitle Book subtitle (optional)
 * @property rating Average rating from the API
 * @property coverImageUrl URL to the book cover image (optional)
 * @property description Book description (optional)
 * @property publishedDate Publication date (optional)
 */
@Immutable
data class Book(
    val id: String,
    val title: String,
    val author: String,
    val subtitle: String,
    val rating: Double,
    val coverImageUrl: String?,
    val description: String?,
    val publishedDate: String?
)
