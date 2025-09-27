package com.example.reader.data

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val genre: String,
    val rating: Double,
    val coverImageRes: Int,
    val isFavorite: Boolean = false,
)