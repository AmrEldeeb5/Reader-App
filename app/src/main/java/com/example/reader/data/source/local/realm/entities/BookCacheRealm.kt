package com.example.reader.data.source.local.realm.entities

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

/**
 * Realm entity for caching book data.
 * Supports offline mode with expiration management.
 */
class BookCacheRealm : RealmObject {
    @PrimaryKey
    var id: String = ""
    var title: String = ""
    var author: String = ""
    var subtitle: String? = null
    var description: String? = null
    var coverImageUrl: String? = null
    var pageCount: Int? = null
    var publishedDate: String? = null
    var publisher: String? = null
    var language: String? = null
    var isbn: String? = null
    var categories: String? = null // JSON string of categories list
    var category: String = "" // The category this book was fetched under
    var rating: Double = 0.0
    var userRating: Double = 0.0
    var isFavorite: Boolean = false
    var cachedAt: Long = System.currentTimeMillis()
    var expiresAt: Long = System.currentTimeMillis() + CACHE_DURATION

    companion object {
        const val CACHE_DURATION = 3600000L // 1 hour in milliseconds
    }
}

