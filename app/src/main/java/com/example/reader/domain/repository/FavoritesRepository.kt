package com.example.reader.domain.repository

import com.example.reader.domain.model.Book
import com.example.reader.domain.model.Favorite
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing favorite books.
 *
 * This interface defines the contract for favorite book operations,
 * abstracting the underlying data source (Realm database).
 */
interface FavoritesRepository {
    
    /**
     * Add a book to favorites.
     *
     * @param bookId Unique book identifier
     * @param book The book to add to favorites
     * @return Result indicating success or failure
     */
    suspend fun addFavorite(bookId: String, book: Book): Result<Unit>
    
    /**
     * Remove a book from favorites.
     *
     * @param bookId Unique book identifier
     * @return Result indicating success or failure
     */
    suspend fun removeFavorite(bookId: String): Result<Unit>
    
    /**
     * Update the user's rating for a favorited book.
     *
     * @param bookId Unique book identifier
     * @param rating User's rating (1.0 to 5.0)
     * @return Result indicating success or failure
     */
    suspend fun updateRating(bookId: String, rating: Double): Result<Unit>
    
    /**
     * Observe the list of favorite books.
     *
     * @return Flow emitting the current list of Favorites
     */
    fun observeFavorites(): Flow<List<Favorite>>
    
    /**
     * Check if a book is in favorites.
     *
     * @param bookId Unique book identifier
     * @return true if the book is favorited, false otherwise
     */
    suspend fun isFavorite(bookId: String): Boolean
    
    /**
     * Observe the favorite status of a specific book.
     *
     * @param bookId Unique book identifier
     * @return Flow emitting true if favorited, false otherwise
     */
    fun observeFavorite(bookId: String): Flow<Boolean>

    /**
     * Update the reading status of a favorited book.
     *
     * @param bookId Unique book identifier
     * @param status New reading status
     * @return Result indicating success or failure
     */
    suspend fun updateReadingStatus(bookId: String, status: com.example.reader.domain.model.ReadingStatus): Result<Unit>
}
