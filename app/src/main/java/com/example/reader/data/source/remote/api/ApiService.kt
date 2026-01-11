package com.example.reader.data.source.remote.api

import com.example.reader.data.source.remote.api.dto.BookItemDto
import com.example.reader.data.source.remote.api.dto.BooksResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service interface for Google Books API.
 */
interface ApiService {
    @GET("volumes")
    suspend fun getBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 40,
        @Query("printType") printType: String = "books",
        @Query("orderBy") orderBy: String = "relevance"
    ): BooksResponse

    /**
     * Get a specific book by its volume ID.
     */
    @GET("volumes/{volumeId}")
    suspend fun getBookById(
        @Path("volumeId") volumeId: String
    ): BookItemDto
}
