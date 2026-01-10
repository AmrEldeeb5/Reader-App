package com.example.reader.data.source.local.realm

import com.example.reader.data.source.local.realm.entities.BookCacheRealm
import com.example.reader.domain.model.Book
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for book caching operations using Realm.
 * Provides offline support and data persistence.
 */
@Singleton
class BookCacheDataSource @Inject constructor(
    private val realm: Realm
) {

    /**
     * Get cached books for a specific category that haven't expired.
     */
    fun getCachedBooks(category: String): List<BookCacheRealm> {
        val currentTime = System.currentTimeMillis()
        return realm.query<BookCacheRealm>(
            "category == $0 AND expiresAt > $1",
            category,
            currentTime
        ).find()
    }

    /**
     * Get all cached books for a category (even expired) - for offline mode.
     */
    fun getAllCachedBooks(category: String): List<BookCacheRealm> {
        return realm.query<BookCacheRealm>("category == $0", category).find()
    }

    /**
     * Get a specific book by ID.
     */
    fun getBookById(bookId: String): BookCacheRealm? {
        return realm.query<BookCacheRealm>("id == $0", bookId).first().find()
    }

    /**
     * Observe a specific book.
     */
    fun observeBookById(bookId: String): Flow<BookCacheRealm?> {
        return realm.query<BookCacheRealm>("id == $0", bookId)
            .asFlow()
            .map { it.list.firstOrNull() }
    }

    /**
     * Search cached books by query.
     */
    fun searchBooks(query: String, limit: Int = 50): List<BookCacheRealm> {
        return realm.query<BookCacheRealm>(
            "title CONTAINS[c] $0 OR author CONTAINS[c] $0 OR description CONTAINS[c] $0",
            query
        ).limit(limit).find()
    }

    /**
     * Get favorite books from cache.
     */
    fun observeFavoriteBooks(): Flow<List<BookCacheRealm>> {
        return realm.query<BookCacheRealm>("isFavorite == true")
            .asFlow()
            .map { it.list }
    }

    /**
     * Cache books (upsert).
     */
    suspend fun cacheBooks(books: List<Book>, category: String) {
        realm.write {
            books.forEach { book ->
                val cached = query<BookCacheRealm>("id == $0", book.id).first().find()

                if (cached != null) {
                    // Update existing
                    cached.apply {
                        title = book.title
                        author = book.author
                        subtitle = book.subtitle
                        description = book.description
                        coverImageUrl = book.coverImageUrl
                        publishedDate = book.publishedDate
                        this.category = category
                        rating = book.rating
                        cachedAt = System.currentTimeMillis()
                        expiresAt = System.currentTimeMillis() + BookCacheRealm.CACHE_DURATION
                    }
                } else {
                    // Insert new
                    copyToRealm(BookCacheRealm().apply {
                        id = book.id
                        title = book.title
                        author = book.author
                        subtitle = book.subtitle
                        description = book.description
                        coverImageUrl = book.coverImageUrl
                        publishedDate = book.publishedDate
                        this.category = category
                        rating = book.rating
                        isFavorite = false
                        cachedAt = System.currentTimeMillis()
                        expiresAt = System.currentTimeMillis() + BookCacheRealm.CACHE_DURATION
                    })
                }
            }
        }
    }

    /**
     * Cache a single book.
     */
    suspend fun cacheBook(book: Book, category: String) {
        cacheBooks(listOf(book), category)
    }

    /**
     * Update favorite status.
     */
    suspend fun updateFavoriteStatus(bookId: String, isFavorite: Boolean) {
        realm.write {
            query<BookCacheRealm>("id == $0", bookId).first().find()?.apply {
                this.isFavorite = isFavorite
            }
        }
    }

    /**
     * Update user rating.
     */
    suspend fun updateUserRating(bookId: String, rating: Double) {
        realm.write {
            query<BookCacheRealm>("id == $0", bookId).first().find()?.apply {
                this.userRating = rating
            }
        }
    }

    /**
     * Delete expired cache entries (except favorites).
     */
    suspend fun clearExpiredCache(): Int {
        val currentTime = System.currentTimeMillis()
        return realm.write {
            val expiredBooks = query<BookCacheRealm>(
                "expiresAt < $0 AND isFavorite == false",
                currentTime
            ).find()
            val count = expiredBooks.size
            delete(expiredBooks)
            count
        }
    }

    /**
     * Delete all cache for a category (except favorites).
     */
    suspend fun clearCategoryCache(category: String) {
        realm.write {
            val books = query<BookCacheRealm>(
                "category == $0 AND isFavorite == false",
                category
            ).find()
            delete(books)
        }
    }

    /**
     * Delete all cache (except favorites).
     */
    suspend fun clearAllCache() {
        realm.write {
            val books = query<BookCacheRealm>("isFavorite == false").find()
            delete(books)
        }
    }

    /**
     * Get cache statistics.
     */
    fun getCacheCount(): Long {
        return realm.query<BookCacheRealm>().count().find()
    }

    /**
     * Convert BookCacheRealm to domain Book.
     */
    fun BookCacheRealm.toDomain(): Book {
        return Book(
            id = id,
            title = title,
            author = author,
            subtitle = subtitle ?: "",
            description = description,
            coverImageUrl = coverImageUrl,
            publishedDate = publishedDate,
            rating = rating
        )
    }
}

