package com.example.reader.data.model

// Response wrapper
data class BooksResponse(
    val items: List<BookItem>?
)

data class BookItem(
    val id: String,
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String?,
    val authors: List<String>?,
    val subtitle: String?,
    val averageRating: Double?,
    val imageLinks: ImageLinks?
)

data class ImageLinks(
    val thumbnail: String?
)
data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val subtitle: String,
    val rating: Double,
    val coverImageUrl: String?,
    val isFavorite: Boolean = false
)