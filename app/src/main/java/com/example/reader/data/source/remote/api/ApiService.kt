package com.example.reader.data.source.remote.api

import com.example.reader.data.source.remote.api.dto.BooksResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for Google Books API.
 */
interface ApiService {
    @GET("volumes")
    suspend fun getBooks(
        @Query("q") query: String,
        @Query("langRestrict") lang: String = "en",
        @Query("maxResults") maxResults: Int = 30
    ): BooksResponse

    @GET("volumes")
    suspend fun getBooksByCategory(
        @Query("q") category: String,
        @Query("langRestrict") lang: String = "en",
        @Query("orderBy") orderBy: String = "relevance",
        @Query("maxResults") maxResults: Int = 40
    ): BooksResponse
}
