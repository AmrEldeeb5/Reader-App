package com.example.reader.domain.repository

import com.example.reader.domain.model.Book
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for book-related operations.
 *
 * This interface defines the contract for book data operations,
 * abstracting the underlying data source (Google Books API).
 */
interface BookRepository {
    
    /**
     * Search for books using a query string.
     *
     * @param query Search query string
     * @return Result containing a list of Books on success, or an exception on failure
     */
    suspend fun searchBooks(query: String): Result<List<Book>>
    
    /**
     * Get books by category.
     *
     * @param category Category name (e.g., "fiction", "science", "romance")
     * @return Result containing a list of Books on success, or an exception on failure
     */
    suspend fun getBooksByCategory(category: String): Result<List<Book>>
    
    /**
     * Get a specific book by its ID.
     *
     * @param bookId Unique book identifier
     * @return Result containing the Book on success, or an exception on failure
     */
    suspend fun getBookById(bookId: String): Result<Book>
    
    /**
     * Observe the current list of books.
     *
     * @return Flow emitting the current list of Books
     */
    fun observeBooks(): Flow<List<Book>>
}
