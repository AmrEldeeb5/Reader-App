package com.example.reader.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.data.api.RetrofitInstance
import com.example.reader.data.model.Book
import com.example.reader.data.model.BookItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchState(
    val isLoading: Boolean = false,
    val books: List<Book> = emptyList(),
    val errorMessage: String? = null,
    val hasSearched: Boolean = false
)

class ExploreViewModel : ViewModel() {
    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

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

            try {
                val response = RetrofitInstance.api.getBooks(query)

                val books = response.items?.mapNotNull { bookItem ->
                    val book = bookItem.toBook()
                    // Filter out books with "Unknown" title or author
                    if (book.title != "Unknown" && book.author != "Unknown") {
                        book
                    } else {
                        null
                    }
                } ?: emptyList()

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
            } catch (e: Exception) {
                _searchState.value = SearchState(
                    hasSearched = true,
                    errorMessage = "Failed to search books. Please check your internet connection."
                )
            }
        }
    }

    fun toggleFavorite(bookId: Int) {
        _searchState.value = _searchState.value.copy(
            books = _searchState.value.books.map { book ->
                if (book.id == bookId) {
                    book.copy(isFavorite = !book.isFavorite)
                } else {
                    book
                }
            }
        )
    }

    fun updateUserRating(bookId: Int, rating: Double) {
        _searchState.value = _searchState.value.copy(
            books = _searchState.value.books.map { book ->
                if (book.id == bookId) {
                    book.copy(userRating = rating)
                } else {
                    book
                }
            }
        )
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchState.value = SearchState()
    }

    private fun BookItem.toBook() = Book(
        id = id.hashCode(),
        title = volumeInfo.title ?: "Unknown",
        author = volumeInfo.authors?.joinToString(", ") ?: "Unknown",
        subtitle = volumeInfo.subtitle ?: "",
        rating = volumeInfo.averageRating ?: 0.0,
        coverImageUrl = volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://"),
        isFavorite = false,
        description = volumeInfo.description
    )
}