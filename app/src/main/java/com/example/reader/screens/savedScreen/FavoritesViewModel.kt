package com.example.reader.screens.saved

import androidx.lifecycle.ViewModel
import com.example.reader.data.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Shared ViewModel for managing favorite books across the app
 * This ensures favorites persist across screen navigation
 * Uses Koin for dependency injection as a singleton
 */
class FavoritesViewModel : ViewModel() {

    private val _favoriteBooks = MutableStateFlow<List<Book>>(emptyList())
    val favoriteBooks: StateFlow<List<Book>> = _favoriteBooks.asStateFlow()

    fun addFavorite(book: Book) {
        val currentFavorites = _favoriteBooks.value.toMutableList()

        // Check if book already exists
        if (currentFavorites.none { it.id == book.id }) {
            currentFavorites.add(book.copy(isFavorite = true))
            _favoriteBooks.value = currentFavorites
        }
    }

    fun removeFavorite(bookId: Int) {
        _favoriteBooks.value = _favoriteBooks.value.filter { it.id != bookId }
    }

    fun toggleFavorite(book: Book) {
        if (isFavorite(book.id)) {
            removeFavorite(book.id)
        } else {
            addFavorite(book)
        }
    }

    fun isFavorite(bookId: Int): Boolean {
        return _favoriteBooks.value.any { it.id == bookId }
    }

    fun updateUserRating(bookId: Int, rating: Double) {
        _favoriteBooks.value = _favoriteBooks.value.map { book ->
            if (book.id == bookId) {
                book.copy(userRating = rating)
            } else {
                book
            }
        }
    }
}