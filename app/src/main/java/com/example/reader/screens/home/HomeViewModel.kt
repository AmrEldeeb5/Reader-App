package com.example.reader.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.domain.model.Book
import com.example.reader.domain.repository.BookRepository
import com.example.reader.domain.repository.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for category books screen.
 */
data class CategoryBooksState(
    val isLoading: Boolean = false,
    val books: List<Book> = emptyList(),
    val error: String? = null
)

/**
 * ViewModel for home screen using Clean Architecture.
 *
 * Manages book categories and favorites through repositories.
 *
 * @property bookRepository Repository for book operations
 * @property favoritesRepository Repository for favorites operations
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {
    
    private val _selectedCategory = MutableStateFlow("novels")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _booksState = MutableStateFlow(CategoryBooksState())
    val booksState: StateFlow<CategoryBooksState> = _booksState.asStateFlow()

    init {
        loadBooksByCategory("novels")
    }

    /**
     * Select a category and load its books.
     *
     * @param category Category name to load
     */
    fun selectCategory(category: String) {
        if (_selectedCategory.value != category) {
            _selectedCategory.value = category
            loadBooksByCategory(category)
        }
    }

    private fun loadBooksByCategory(category: String) {
        viewModelScope.launch {
            _booksState.value = CategoryBooksState(isLoading = true)

            val result = bookRepository.getBooksByCategory(category)
            
            result.fold(
                onSuccess = { books ->
                    _booksState.value = CategoryBooksState(books = books)
                },
                onFailure = { error ->
                    _booksState.value = CategoryBooksState(
                        error = error.message ?: "Failed to load books"
                    )
                }
            )
        }
    }

    /**
     * Toggle favorite status for a book.
     *
     * @param book The book to toggle
     */
    fun toggleFavorite(book: Book) {
        viewModelScope.launch {
            val isFavorite = favoritesRepository.isFavorite(book.id)
            
            if (isFavorite) {
                favoritesRepository.removeFavorite(book.id)
            } else {
                favoritesRepository.addFavorite(book.id, book)
            }
        }
    }

    /**
     * Update user rating for a book.
     *
     * @param bookId Book identifier
     * @param rating User's rating (1.0 to 5.0)
     */
    fun updateUserRating(bookId: String, rating: Double) {
        viewModelScope.launch {
            favoritesRepository.updateRating(bookId, rating)
        }
    }

    /**
     * Check if a book is in favorites.
     *
     * @param bookId Book identifier
     * @return true if favorited, false otherwise
     */
    suspend fun isFavorite(bookId: String): Boolean {
        return favoritesRepository.isFavorite(bookId)
    }
}