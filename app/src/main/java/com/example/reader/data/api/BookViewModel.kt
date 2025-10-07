package com.example.reader.data.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.data.model.Book
import com.example.reader.data.model.BookItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookViewModel: ViewModel() {
    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    // Separate flow for favorite books
    private val _favoriteBooks = MutableStateFlow<List<Book>>(emptyList())
    val favoriteBooks: StateFlow<List<Book>> = _favoriteBooks.asStateFlow()

    init {
        fetchBooks()
    }

    private fun fetchBooks() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getBooks("none")
                _books.value = response.items?.mapNotNull { bookItem ->
                    val book = bookItem.toBook()
                    // Filter out books with "Unknown" title or author
                    if (book.title != "Unknown" && book.author != "Unknown") {
                        book
                    } else {
                        null
                    }
                } ?: emptyList()
            } catch (_: Exception) {
                _books.value = emptyList()
            }
        }
    }

    fun toggleFavorite(bookId: Int) {
        // Update main books list
        _books.value = _books.value.map { book ->
            if (book.id == bookId) {
                book.copy(isFavorite = !book.isFavorite)
            } else {
                book
            }
        }

        // Update favorites list
        updateFavoritesList()
    }

    private fun updateFavoritesList() {
        _favoriteBooks.value = _books.value.filter { it.isFavorite }
    }

    fun updateUserRating(bookId: Int, rating: Double) {
        _books.value = _books.value.map { book ->
            if (book.id == bookId) {
                book.copy(userRating = rating)
            } else {
                book
            }
        }

        // Update favorites list if the rated book is a favorite
        updateFavoritesList()
    }

    private fun BookItem.toBook() = Book(
        id = id.hashCode(),
        title = volumeInfo.title ?: "Unknown",
        author = volumeInfo.authors?.joinToString(", ") ?: "Unknown",
        subtitle = volumeInfo.subtitle ?: "",
        rating = volumeInfo.averageRating ?: 0.0,
        coverImageUrl = volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://"),
        isFavorite = false
    )
}