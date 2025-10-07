package com.example.reader.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.data.api.RetrofitInstance
import com.example.reader.data.model.Book
import com.example.reader.data.model.BookItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CategoryBooksState(
    val isLoading: Boolean = false,
    val books: List<Book> = emptyList(),
    val error: String? = null
)

class HomeViewModel : ViewModel() {
    private val _selectedCategory = MutableStateFlow("novels")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _booksState = MutableStateFlow(CategoryBooksState())
    val booksState: StateFlow<CategoryBooksState> = _booksState.asStateFlow()

    init {
        loadBooksByCategory("novels")
    }

    fun selectCategory(category: String) {
        if (_selectedCategory.value != category) {
            _selectedCategory.value = category
            loadBooksByCategory(category)
        }
    }

    private fun loadBooksByCategory(category: String) {
        viewModelScope.launch {
            _booksState.value = CategoryBooksState(isLoading = true)

            try {
                // Build query based on category
                val query = when (category.lowercase()) {
                    "novels" -> "subject:fiction"
                    "self love", "self-love", "selflove" -> "subject:self-help+self-love"
                    "science" -> "subject:science"
                    "romance" -> "subject:romance"
                    "fantasy" -> "subject:fantasy"
                    "mystery" -> "subject:mystery"
                    "biography" -> "subject:biography"
                    "history" -> "subject:history"
                    "psychology" -> "subject:psychology"
                    "business" -> "subject:business"
                    "technology" -> "subject:technology"
                    "philosophy" -> "subject:philosophy"
                    else -> "subject:$category"
                }

                val response = RetrofitInstance.api.getBooksByCategory(query)

                val books = response.items?.mapNotNull { bookItem ->
                    val book = bookItem.toBook()
                    // Filter out books with "Unknown" title or author
                    if (book.title != "Unknown" && book.author != "Unknown") {
                        book
                    } else {
                        null
                    }
                } ?: emptyList()

                _booksState.value = CategoryBooksState(books = books)
            } catch (e: Exception) {
                _booksState.value = CategoryBooksState(
                    error = "Failed to load books: ${e.localizedMessage}"
                )
            }
        }
    }

    fun toggleFavorite(bookId: Int) {
        _booksState.value = _booksState.value.copy(
            books = _booksState.value.books.map { book ->
                if (book.id == bookId) {
                    book.copy(isFavorite = !book.isFavorite)
                } else {
                    book
                }
            }
        )
    }

    fun updateUserRating(bookId: Int, rating: Double) {
        _booksState.value = _booksState.value.copy(
            books = _booksState.value.books.map { book ->
                if (book.id == bookId) {
                    book.copy(userRating = rating)
                } else {
                    book
                }
            }
        )
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