package com.example.reader.data.api

import com.example.reader.data.model.Book
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("volumes")
    suspend fun getBooks(
        @Query("q") query: String,  // search term (e.g. "novels")
        @Query("langRestrict") lang: String = "en", // only English
        @Query("maxResults") maxResults: Int = 20, // number of results
        @Query("key") apiKey: String = "AIzaSyCsZOXLJRT6eTIyCTwn7_ju23z-ofmDnRM"
    ): Book
}
