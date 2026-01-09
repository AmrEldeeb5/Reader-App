package com.example.reader.screens.explore

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
 * UI state for book search.
 */
data class SearchState(
    val isLoading: Boolean = false,
    val books: List<Book> = emptyList(),
    val errorMessage: String? = null,
    val hasSearched: Boolean = false
)

/**
 * ViewModel for explore/search screen using Clean Architecture.
 *
 * Manages book search and favorites through repositories.
 *
 * @property bookRepository Repository for book operations
 * @property favoritesRepository Repository for favorites operations
 */
@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {
    
    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /**
     * Update the search query text.
     *
     * @param query New search query
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Search for books by query.
     *
     * @param query Search term
     */
    fun searchBooks(query: String) {
        if (query.isBlank()) {
            _searchState.value = SearchState(
                hasSearched = true,
                errorMessage = "Please enter a search term"
            )
            return
        }

        viewModelScope.launch {
            _searchState.value = SearchState(isLoading = true, hasSearched = true)

            val result = bookRepository.searchBooks(query)
            
            result.fold(
                onSuccess = { books ->
                    _searchState.value = if (books.isEmpty()) {
                        SearchState(
                            hasSearched = true,
                            errorMessage = "No books found for \"$query\". Try a different search term."
                        )
                    } else {
                        SearchState(
                            hasSearched = true,
                            books = books
                        )
                    }
                },
                onFailure = { error ->
                    _searchState.value = SearchState(
                        hasSearched = true,
                        errorMessage = error.message ?: "Failed to search books. Please check your internet connection."
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
     * Clear the search query and results.
     */
    fun clearSearch() {
        _searchQuery.value = ""
        _searchState.value = SearchState()
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