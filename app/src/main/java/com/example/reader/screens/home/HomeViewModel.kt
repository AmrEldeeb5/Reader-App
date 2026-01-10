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
 * UI state for category books screen with pagination support.
 */
data class CategoryBooksState(
    val isLoading: Boolean = false,
    val books: List<Book> = emptyList(),
    val error: String? = null,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 0,
    val hasMore: Boolean = true,
    val isRefreshing: Boolean = false
)

/**
 * State for the last selected book (for Discovery screen).
 */
data class LastSelectedBookState(
    val bookId: String? = null,
    val coverUrl: String? = null,
    val title: String? = null,
    val description: String? = null,
    val categoryName: String? = null
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

    private val _lastSelectedBook = MutableStateFlow(LastSelectedBookState())
    val lastSelectedBook: StateFlow<LastSelectedBookState> = _lastSelectedBook.asStateFlow()

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

    /**
     * Refresh current category books (for pull-to-refresh).
     */
    fun refreshBooks() {
        viewModelScope.launch {
            _booksState.value = _booksState.value.copy(isRefreshing = true)

            val result = bookRepository.getBooksByCategory(_selectedCategory.value)

            result.fold(
                onSuccess = { books ->
                    _booksState.value = CategoryBooksState(
                        books = books,
                        hasMore = books.isNotEmpty()
                    )
                },
                onFailure = { error ->
                    _booksState.value = _booksState.value.copy(
                        isRefreshing = false,
                        error = error.message
                    )
                }
            )
        }
    }

    /**
     * Load more books (pagination).
     * Note: Requires API pagination support.
     */
    fun loadMoreBooks() {
        val currentState = _booksState.value
        if (currentState.isLoadingMore || !currentState.hasMore) return

        viewModelScope.launch {
            _booksState.value = currentState.copy(isLoadingMore = true)

            // Note: This is a placeholder for pagination
            // Actual implementation depends on API support
            // For now, we just update the state
            _booksState.value = currentState.copy(
                isLoadingMore = false,
                hasMore = false // No more pages available yet
            )
        }
    }

    private fun loadBooksByCategory(category: String) {
        viewModelScope.launch {
            _booksState.value = CategoryBooksState(isLoading = true)

            val result = bookRepository.getBooksByCategory(category)
            
            result.fold(
                onSuccess = { books ->
                    _booksState.value = CategoryBooksState(
                        books = books,
                        hasMore = books.isNotEmpty()
                    )
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
     * Set the last selected book for the Discovery screen.
     *
     * @param bookId Book identifier
     * @param coverUrl Book cover URL
     * @param title Book title
     * @param description Book description
     * @param categoryName Category display name
     */
    fun setLastSelectedBook(
        bookId: String,
        coverUrl: String?,
        title: String,
        description: String?,
        categoryName: String
    ) {
        _lastSelectedBook.value = LastSelectedBookState(
            bookId = bookId,
            coverUrl = coverUrl,
            title = title,
            description = description,
            categoryName = categoryName
        )
    }

    /**
     * Clear the last selected book state.
     */
    fun clearLastSelectedBook() {
        _lastSelectedBook.value = LastSelectedBookState()
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