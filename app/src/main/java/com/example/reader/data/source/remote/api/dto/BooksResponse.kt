package com.example.reader.data.source.remote.api.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO for Google Books API response.
 *
 * @property items List of book items
 * @property kind API resource type
 * @property totalItems Total number of items
 */
data class BooksResponse(
    @SerializedName("items")
    val items: List<BookItemDto>?,
    @SerializedName("kind")
    val kind: String?,
    @SerializedName("totalItems")
    val totalItems: Int?
)

/**
 * DTO for a single book item from the API.
 *
 * @property id Unique book identifier
 * @property volumeInfo Volume information
 */
data class BookItemDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("volumeInfo")
    val volumeInfo: VolumeInfoDto
)

/**
 * DTO for book volume information.
 *
 * @property title Book title
 * @property authors List of authors
 * @property subtitle Book subtitle
 * @property averageRating Average rating
 * @property imageLinks Image links
 * @property description Book description
 * @property publishedDate Publication date
 */
data class VolumeInfoDto(
    @SerializedName("title")
    val title: String?,
    @SerializedName("authors")
    val authors: List<String>?,
    @SerializedName("subtitle")
    val subtitle: String?,
    @SerializedName("averageRating")
    val averageRating: Double?,
    @SerializedName("imageLinks")
    val imageLinks: ImageLinksDto?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("publishedDate")
    val publishedDate: String?
)

/**
 * DTO for book cover image links.
 *
 * @property thumbnail Thumbnail URL
 * @property smallThumbnail Small thumbnail URL
 */
data class ImageLinksDto(
    @SerializedName("thumbnail")
    val thumbnail: String?,
    @SerializedName("smallThumbnail")
    val smallThumbnail: String?
)
