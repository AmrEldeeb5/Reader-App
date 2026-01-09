package com.example.reader.data.repository

import com.example.reader.data.source.remote.api.ApiService
import com.example.reader.data.mapper.BookMapper
import com.example.reader.domain.error.toAppError
import com.example.reader.domain.model.Book
import com.example.reader.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of BookRepository using Google Books API.
 *
 * @property apiService Retrofit API service
 * @property bookMapper Mapper to convert DTOs to domain models
 */
@Singleton
class BookRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val bookMapper: BookMapper
) : BookRepository {
    
    private val _books = MutableStateFlow<List<Book>>(emptyList())
    
    override suspend fun searchBooks(query: String): Result<List<Book>> {
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
            
            _books.value = books
            Result.success(books)
        } catch (e: Exception) {
            Result.failure(e.toAppError())
        }
    }
    
    override suspend fun getBooksByCategory(category: String): Result<List<Book>> {
        return try {
            val query = mapCategoryToQuery(category)
            val response = apiService.getBooksByCategory(query)
            val books = response.items?.mapNotNull { bookItem ->
                val book = bookMapper.toDomain(bookItem)
                // Filter out books with "Unknown" title or author
                if (book.title != "Unknown" && book.author != "Unknown") {
                    book
                } else {
                    null
                }
            } ?: emptyList()
            
            _books.value = books
            Result.success(books)
        } catch (e: Exception) {
            Result.failure(e.toAppError())
        }
    }
    
    override suspend fun getBookById(bookId: String): Result<Book> {
        return try {
            // Try to find in cached books first
            val cachedBook = _books.value.find { it.id == bookId }
            if (cachedBook != null) {
                Result.success(cachedBook)
            } else {
                // If not found, search by ID
                val response = apiService.getBooks(bookId)
                val book = response.items?.firstOrNull()?.let { bookMapper.toDomain(it) }
                if (book != null) {
                    Result.success(book)
                } else {
                    Result.failure(Exception("Book not found"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e.toAppError())
        }
    }
    
    override fun observeBooks(): Flow<List<Book>> {
        return _books.asStateFlow()
    }
    
    /**
     * Map category name to Google Books API query.
     */
    private fun mapCategoryToQuery(category: String): String {
        return when (category.lowercase()) {
            "novels" -> "subject:fiction"
            "self love", "self-love", "selflove" -> "subject:self-help+self-love"
            "science" -> "subject:science"
            "romance" -> "subject:romance"
            "fantasy" -> "subject:fantasy"
            "mystery" -> "subject:mystery"
            "biography" -> "subject:biography"
            "history" -> "subject:history"
            "psychology" -> "subject:psychology"
            "business" -> "subject:business"
            "technology" -> "subject:technology"
            "philosophy" -> "subject:philosophy"
            else -> "subject:$category"
        }
    }
}
