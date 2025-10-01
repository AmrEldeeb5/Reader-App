package com.example.reader.data.model

// Response wrapper - this is what the API returns
data class BooksResponse(
    val items: List<BookItem>? = null,
    val kind: String? = null,
    val totalItems: Int? = null
)

// Each book item from the API
data class BookItem(
    val id: String,
    val volumeInfo: VolumeInfo
)

// Book information
data class VolumeInfo(
    val title: String? = null,
    val authors: List<String>? = null,
    val subtitle: String? = null,
    val averageRating: Double? = null,
    val imageLinks: ImageLinks? = null,
    val description: String? = null,
    val publishedDate: String? = null
)

// Book cover images
data class ImageLinks(
    val thumbnail: String? = null,
    val smallThumbnail: String? = null
)

// app's Book model (UI model)
data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val subtitle: String,
    val rating: Double,  // API rating
    val coverImageUrl: String?,
    val isFavorite: Boolean = false,
    val userRating: Double? = null  // User's personal rating
)