package com.example.reader.data.repository

import com.example.reader.data.source.remote.api.ApiService
import com.example.reader.data.mapper.BookMapper
import com.example.reader.data.source.local.realm.BookCacheDataSource
import com.example.reader.data.source.local.realm.*
import com.example.reader.domain.error.AppError
import com.example.reader.domain.error.toAppError
import com.example.reader.domain.model.Book
import com.example.reader.domain.repository.BookRepository
import com.example.reader.utils.NetworkConnectivityManager
import com.example.reader.utils.AppLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of BookRepository using Google Books API with Realm caching.
 * Supports offline mode and automatic cache management.
 *
 * @property apiService Retrofit API service
 * @property bookMapper Mapper to convert DTOs to domain models
 * @property networkManager Network connectivity manager
 * @property bookCacheDataSource Realm data source for caching
 * @property appLogger Logger for errors and events
 */
@Singleton
class BookRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val bookMapper: BookMapper,
    private val networkManager: NetworkConnectivityManager,
    private val bookCacheDataSource: BookCacheDataSource,
    private val appLogger: AppLogger
) : BookRepository {
    
    private val _books = MutableStateFlow<List<Book>>(emptyList())
    
    override suspend fun searchBooks(query: String): Result<List<Book>> {
        // Try cache first for offline mode
        if (!networkManager.isNetworkAvailable()) {
            appLogger.logInfo("Offline mode: Searching in cache for '$query'")
            val cachedBooks = bookCacheDataSource.searchBooks(query)

            return if (cachedBooks.isNotEmpty()) {
                val books: List<Book> = with(bookCacheDataSource) {
                    cachedBooks.map { it.toDomain() }
                }
                Result.success(books)
            } else {
                Result.failure(AppError.NetworkError("No internet connection and no cached results for '$query'"))
            }
        }

        // Online mode - fetch from API
        return try {
            val response = apiService.getBooks(query)
            val books = response.items?.mapNotNull { bookItem ->
                val book = bookMapper.toDomain(bookItem)
                // Filter out books with "Unknown" title or author
                if (book.title != "Unknown" && book.author != "Unknown") {
                    book
                } else {
                    null
                }
            } ?: emptyList()
            
            // Cache the search results
            bookCacheDataSource.cacheBooks(books, "search:$query")

            _books.value = books
            appLogger.logInfo("Search completed: Found ${books.size} books for '$query'")
            Result.success(books)
        } catch (e: Exception) {
            appLogger.logError("Search failed for '$query'", e)
            Result.failure(e.toAppError())
        }
    }
    
    override suspend fun getBooksByCategory(category: String): Result<List<Book>> {
        // Check cache first (valid cache)
        val cachedBooks = bookCacheDataSource.getCachedBooks(category)
        if (cachedBooks.isNotEmpty()) {
            val books: List<Book> = with(bookCacheDataSource) {
                cachedBooks.map { it.toDomain() }
            }
            appLogger.logInfo("Returning ${books.size} cached books for category '$category'")
            return Result.success(books)
        }

        // Check network connectivity
        if (!networkManager.isNetworkAvailable()) {
            // Offline mode - return all cached books (even expired)
            appLogger.logInfo("Offline mode: Returning all cached books for '$category'")
            val allCached = bookCacheDataSource.getAllCachedBooks(category)

            return if (allCached.isNotEmpty()) {
                val books: List<Book> = with(bookCacheDataSource) {
                    allCached.map { it.toDomain() }
                }
                Result.success(books)
            } else {
                Result.failure(AppError.NetworkError("No internet connection and no cached data for '$category'"))
            }
        }

        // Online mode - fetch from API
        return try {
            val query = mapCategoryToQuery(category)
            appLogger.logInfo("Fetching books for category '$category' with query: '$query'")
            val response = apiService.getBooks(query) // Use simpler getBooks instead
            val books = response.items?.mapNotNull { bookItem ->
                val book = bookMapper.toDomain(bookItem)
                // Filter out books with "Unknown" title or author
                if (book.title != "Unknown" && book.author != "Unknown") {
                    book
                } else {
                    null
                }
            } ?: emptyList()
            
            // Cache the results
            bookCacheDataSource.cacheBooks(books, category)

            _books.value = books
            appLogger.logInfo("Fetched ${books.size} books for category '$category'")
            Result.success(books)
        } catch (e: Exception) {
            appLogger.logError("Failed to fetch books for category '$category'", e)
            Result.failure(e.toAppError())
        }
    }
    
    override suspend fun getBookById(bookId: String): Result<Book> {
        // Try cache first
        val cachedBook = bookCacheDataSource.getBookById(bookId)
        if (cachedBook != null) {
            appLogger.logInfo("Returning cached book: $bookId")
            return with(bookCacheDataSource) {
                Result.success(cachedBook.toDomain())
            }
        }

        // Check network connectivity
        if (!networkManager.isNetworkAvailable()) {
            // Try in-memory cache as last resort
            val memoryBook = _books.value.find { it.id == bookId }
            return if (memoryBook != null) {
                Result.success(memoryBook)
            } else {
                Result.failure(AppError.NetworkError("No internet connection and book not found in cache"))
            }
        }

        // Online mode - fetch from API using the specific volume endpoint
        return try {
            val bookDto = apiService.getBookById(bookId)
            val book = bookMapper.toDomain(bookDto)

            // Cache the book
            bookCacheDataSource.cacheBook(book, "details")
            appLogger.logInfo("Fetched book details: ${book.title}")
            Result.success(book)
        } catch (e: Exception) {
            appLogger.logError("Failed to fetch book by ID: $bookId", e)
            Result.failure(e.toAppError())
        }
    }
    
    override fun observeBooks(): Flow<List<Book>> {
        return _books.asStateFlow()
    }
    
    /**
     * Clear expired cache entries.
     */
    suspend fun clearExpiredCache(): Int {
        return try {
            val count = bookCacheDataSource.clearExpiredCache()
            appLogger.logInfo("Cleared $count expired cache entries")
            count
        } catch (e: Exception) {
            appLogger.logError("Failed to clear expired cache", e)
            0
        }
    }

    /**
     * Map category name to Google Books API query.
     */
    private fun mapCategoryToQuery(category: String): String {
        // Use simple keyword searches - Google Books API works best with plain terms
        return when (category.lowercase()) {
            "novels" -> "fiction books"
            "self love", "self-love", "selflove" -> "self help motivation"
            "science" -> "science"
            "romance" -> "romance"
            "fantasy" -> "fantasy"
            "mystery" -> "mystery"
            "biography" -> "biography"
            "history" -> "history"
            "psychology" -> "psychology"
            "business" -> "business"
            "technology" -> "technology programming"
            "philosophy" -> "philosophy"
            else -> category
        }
    }
}
