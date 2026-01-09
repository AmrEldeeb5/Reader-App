package com.example.reader.data.source.local.realm

import com.example.reader.data.source.local.realm.entities.FavoriteBookRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for Realm database operations on favorite books.
 *
 * This class provides CRUD operations for favorite books using Realm.
 *
 * @property realm Realm database instance
 */
@Singleton
class RealmDataSource @Inject constructor(
    private val realm: Realm
) {
    
    /**
     * Save a favorite book to the database.
     *
     * @param favoriteBook The favorite book entity to save
     */
    suspend fun saveFavoriteBook(favoriteBook: FavoriteBookRealm) {
        realm.write {
            // Check if book already exists
            val existingBook = query<FavoriteBookRealm>("id == $0", favoriteBook.id)
                .first()
                .find()
            
            if (existingBook == null) {
                copyToRealm(favoriteBook)
            } else {
                // Update existing book
                existingBook.title = favoriteBook.title
                existingBook.author = favoriteBook.author
                existingBook.subtitle = favoriteBook.subtitle
                existingBook.rating = favoriteBook.rating
                existingBook.coverImageUrl = favoriteBook.coverImageUrl
                existingBook.userRating = favoriteBook.userRating
                existingBook.description = favoriteBook.description
            }
        }
    }
    
    /**
     * Remove a favorite book from the database.
     *
     * @param bookId Unique book identifier
     */
    suspend fun removeFavoriteBook(bookId: String) {
        realm.write {
            val bookToDelete = query<FavoriteBookRealm>("id == $0", bookId)
                .first()
                .find()
            bookToDelete?.let { delete(it) }
        }
    }
    
    /**
     * Update the user rating for a favorite book.
     *
     * @param bookId Unique book identifier
     * @param userRating User's rating
     */
    suspend fun updateBookRating(bookId: String, userRating: Double) {
        realm.write {
            val book = query<FavoriteBookRealm>("id == $0", bookId)
                .first()
                .find()
            book?.userRating = userRating
        }
    }
    
    /**
     * Observe all favorite books.
     *
     * @return Flow emitting the list of favorite books sorted by added timestamp
     */
    fun observeAllFavoriteBooks(): Flow<List<FavoriteBookRealm>> {
        return realm.query<FavoriteBookRealm>()
            .sort("addedTimestamp", Sort.DESCENDING)
            .asFlow()
            .map { results -> results.list.toList() }
    }
    
    /**
     * Check if a book is in favorites.
     *
     * @param bookId Unique book identifier
     * @return true if the book is favorited, false otherwise
     */
    suspend fun isFavorite(bookId: String): Boolean {
        return realm.query<FavoriteBookRealm>("id == $0", bookId)
            .first()
            .find() != null
    }
    
    /**
     * Get a favorite book by ID.
     *
     * @param bookId Unique book identifier
     * @return FavoriteBookRealm or null if not found
     */
    suspend fun getFavoriteBook(bookId: String): FavoriteBookRealm? {
        return realm.query<FavoriteBookRealm>("id == $0", bookId)
            .first()
            .find()
    }
}
