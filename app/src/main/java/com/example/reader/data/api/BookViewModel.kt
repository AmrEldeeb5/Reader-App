package com.example.reader.data.api

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.data.model.Book
import com.example.reader.data.model.BookItem
import kotlinx.coroutines.launch

class BookViewModel: ViewModel() {
    private val _books = MutableLiveData<List<Book>>(emptyList())
    val books: MutableLiveData<List<Book>> = _books

    init {
        fetchBooks()
    }

    private fun fetchBooks() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getBooks("novels")
                _books.value = response.items?.map { it.toBook() } ?: emptyList()
            } catch (e: Exception) {
                // Handle the error
                _books.value = emptyList()
            }
        }
    }

    fun toggleFavorite(bookId: Int) {
        _books.value = _books.value?.map { book ->
            if (book.id == bookId) {
                book.copy(isFavorite = !book.isFavorite)
            } else {
                book
            }
        }
    }

    // Extension function to map API response to Book model
    private fun BookItem.toBook() = Book(
        id = id.hashCode(),
        title = volumeInfo.title ?: "Unknown",
        author = volumeInfo.authors?.joinToString(", ") ?: "Unknown",
        subtitle = volumeInfo.subtitle ?: "",
        rating = volumeInfo.averageRating ?: 0.0,
        coverImageUrl = volumeInfo.imageLinks?.thumbnail,
        isFavorite = false
    )
}