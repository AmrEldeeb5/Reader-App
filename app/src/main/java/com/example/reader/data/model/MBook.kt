package com.example.reader.data.model

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val subtitle: String,
    val rating: Double,
    val coverImageUrl: String?,
    val isFavorite: Boolean = false,
)