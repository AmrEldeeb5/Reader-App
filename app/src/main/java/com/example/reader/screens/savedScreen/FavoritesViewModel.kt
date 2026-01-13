package com.example.reader.screens.savedScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader.domain.model.Book
import com.example.reader.domain.model.Favorite
import com.example.reader.domain.repository.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for favorites screen using Clean Architecture.
 *
 * Manages favorite books with reactive updates through repositories.
 *
 * @property favoritesRepository Repository for favorites operations
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val userPreferencesRepository: com.example.reader.domain.repository.UserPreferencesRepository,
    private val firebaseSyncRepository: com.example.reader.data.repository.FirebaseSyncRepository
) : ViewModel() {

    /**
     * Reactive list of favorite books from Realm database.
     */
    val favoriteBooks: StateFlow<List<Favorite>> = favoritesRepository
        .observeFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentBook = MutableStateFlow<Book?>(null)
    val currentBook: StateFlow<Book?> = _currentBook.asStateFlow()

    private val _readingStats = MutableStateFlow(com.example.reader.domain.model.ReadingStats())
    val readingStats: StateFlow<com.example.reader.domain.model.ReadingStats> = _readingStats.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadReadingStats()
    }

    /**
     * Refresh favorites list (reloads stats).
     */
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            loadReadingStats()
            kotlinx.coroutines.delay(500) // Brief delay for UX
            _isRefreshing.value = false
        }
    }

    private fun loadReadingStats() {
        viewModelScope.launch {
            _readingStats.value = userPreferencesRepository.getReadingStats().copy(
                totalFavorites = favoriteBooks.value.size
            )
        }
    }

    /**
     * Set the currently selected book for navigation.
     *
     * @param book The book to set as current
     */
    fun setCurrentBook(book: Book) {
        _currentBook.value = book
    }

    /**
     * Add a book to favorites.
     *
     * @param book The book to add
     */
    fun addFavorite(book: Book) {
        viewModelScope.launch {
            favoritesRepository.addFavorite(book.id, book)

            // Sync to Firebase if user is signed in
            if (firebaseSyncRepository.isUserSignedIn()) {
                val favorite = favoriteBooks.value.find { it.book.id == book.id }
                favorite?.let {
                    firebaseSyncRepository.syncFavoriteToCloud(
                        bookId = it.bookId,
                        book = it.book,
                        readingStatus = it.readingStatus,
                        userRating = it.userRating,
                        addedTimestamp = it.addedTimestamp
                    )
                }
            }
        }
    }

    /**
     * Remove a book from favorites.
     *
     * @param bookId Book identifier
     */
    fun removeFavorite(bookId: String) {
        viewModelScope.launch {
            favoritesRepository.removeFavorite(bookId)

            // Remove from Firebase if user is signed in
            if (firebaseSyncRepository.isUserSignedIn()) {
                firebaseSyncRepository.removeFavoriteFromCloud(bookId)
            }
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
     * Check if a book is in favorites.
     *
     * @param bookId Book identifier
     * @return true if favorited, false otherwise
     */
    suspend fun isFavorite(bookId: String): Boolean {
        return favoritesRepository.isFavorite(bookId)
    }

    /**
     * Update user rating for a favorite book.
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
     * Update reading status for a favorite book.
     *
     * @param bookId Book identifier
     * @param status New reading status
     */
    fun updateReadingStatus(bookId: String, status: com.example.reader.domain.model.ReadingStatus) {
        viewModelScope.launch {
            favoritesRepository.updateReadingStatus(bookId, status)

            // Track stats when marking as finished
            if (status == com.example.reader.domain.model.ReadingStatus.FINISHED) {
                userPreferencesRepository.incrementBooksRead()
                userPreferencesRepository.updateReadingStreak()
                loadReadingStats()
            }

            // Sync to Firebase if user is signed in
            if (firebaseSyncRepository.isUserSignedIn()) {
                val favorite = favoriteBooks.value.find { it.bookId == bookId }
                favorite?.let {
                    firebaseSyncRepository.syncFavoriteToCloud(
                        bookId = it.bookId,
                        book = it.book,
                        readingStatus = it.readingStatus,
                        userRating = it.userRating,
                        addedTimestamp = it.addedTimestamp
                    )
                }
            }
        }
    }
}

