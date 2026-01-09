package com.example.reader.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.domain.model.Book
import com.example.reader.domain.repository.BookRepository
import com.example.reader.domain.repository.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
 * Manages book search and favorites through repositories with debounced search.
 *
 * @property bookRepository Repository for book operations
 * @property favoritesRepository Repository for favorites operations
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {
    
    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var searchJob: Job? = null

    init {
        // Set up debounced search
        setupDebouncedSearch()
    }

    /**
     * Set up automatic debounced search when query changes.
     */
    private fun setupDebouncedSearch() {
        _searchQuery
            .debounce(500) // Wait 500ms after user stops typing
            .distinctUntilChanged()
            .filter { it.isNotBlank() && it.length >= 2 } // Minimum 2 characters
            .onEach { query ->
                performSearch(query)
            }
            .launchIn(viewModelScope)
    }

    /**
     * Update the search query text.
     * Search will be triggered automatically after debounce period.
     *
     * @param query New search query
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query

        // Show immediate feedback for invalid input
        if (query.isNotBlank() && query.length < 2) {
            _searchState.value = SearchState(
                hasSearched = false,
                errorMessage = "Please enter at least 2 characters"
            )
        } else if (query.isBlank() && _searchState.value.hasSearched) {
            clearSearch()
        }
    }

    /**
     * Search for books by query with validation.
     *
     * @param query Search term
     */
    fun searchBooks(query: String) {
        // Cancel any pending search
        searchJob?.cancel()

        val validatedQuery = query.trim()

        // Validate input
        when {
            validatedQuery.isBlank() -> {
                _searchState.value = SearchState(
                    hasSearched = true,
                    errorMessage = "Please enter a search term"
                )
                return
            }
            validatedQuery.length < 2 -> {
                _searchState.value = SearchState(
                    hasSearched = true,
                    errorMessage = "Search term must be at least 2 characters"
                )
                return
            }
            validatedQuery.length > 100 -> {
                _searchState.value = SearchState(
                    hasSearched = true,
                    errorMessage = "Search term is too long"
                )
                return
            }
        }

        searchJob = viewModelScope.launch {
            performSearch(validatedQuery)
        }
    }

    /**
     * Perform the actual search operation.
     *
     * @param query Validated search query
     */
    private suspend fun performSearch(query: String) {
        _searchState.value = SearchState(isLoading = true, hasSearched = true)

        // Small delay to show loading state
        delay(100)

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

    /**
     * Observe favorite status for a book as a Flow.
     *
     * @param bookId Book identifier
     * @return Flow emitting favorite status
     */
    fun isFavoriteFlow(bookId: String) = favoritesRepository.observeFavorite(bookId)
}