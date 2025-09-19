package com.example.reader.data

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val genre: String,
    val price: String,
    val salePrice: String? = null,
    val rating: Float,
    val coverImageRes: Int,
    val isFavorite: Boolean = false,
    val salePercentage: String? = null
)