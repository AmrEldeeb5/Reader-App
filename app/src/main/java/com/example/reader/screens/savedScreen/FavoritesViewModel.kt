package com.example.reader.screens.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.data.model.Book
import com.example.reader.data.realm.RealmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Shared ViewModel for managing favorite books across the app
 * This ensures favorites persist across screen navigation and app restarts using Realm DB
 * Uses Koin for dependency injection as a singleton
 */
class FavoritesViewModel(private val realmRepository: RealmRepository) : ViewModel() {

    private val _favoriteBooks = MutableStateFlow<List<Book>>(emptyList())
    val favoriteBooks: StateFlow<List<Book>> = _favoriteBooks.asStateFlow()

    // Hold currently selected book for details screen navigation
    private val _currentBook = MutableStateFlow<Book?>(null)
    val currentBook: StateFlow<Book?> = _currentBook.asStateFlow()

    init {
        loadFavoritesFromRealm()
    }

    private fun loadFavoritesFromRealm() {
        viewModelScope.launch {
            realmRepository.getAllFavoriteBooks().collect { books ->
                _favoriteBooks.value = books
            }
        }
    }

    fun setCurrentBook(book: Book) {
        _currentBook.value = book
    }

    fun addFavorite(book: Book) {
        viewModelScope.launch {
            realmRepository.saveFavoriteBook(book)
        }
    }

    fun removeFavorite(bookId: Int) {
        viewModelScope.launch {
            realmRepository.removeFavoriteBook(bookId)
        }
    }

    fun toggleFavorite(book: Book) {
        viewModelScope.launch {
            if (isFavorite(book.id)) {
                removeFavorite(book.id)
            } else {
                addFavorite(book)
            }
        }
    }

    fun isFavorite(bookId: Int): Boolean {
        return _favoriteBooks.value.any { it.id == bookId }
    }

    fun updateUserRating(bookId: Int, rating: Double) {
        viewModelScope.launch {
            realmRepository.updateBookRating(bookId, rating)
        }
    }
}