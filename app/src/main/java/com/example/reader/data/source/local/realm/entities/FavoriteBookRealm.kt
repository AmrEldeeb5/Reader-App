package com.example.reader.data.source.local.realm.entities

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

/**
 * Realm entity for favorite books.
 */
class FavoriteBookRealm : RealmObject {
    @PrimaryKey
    var id: String = ""
    var title: String = ""
    var author: String = ""
    var subtitle: String = ""
    var rating: Double = 0.0
    var coverImageUrl: String? = null
    var userRating: Double? = null
    var description: String? = null
    var publishedDate: String? = null
    var addedTimestamp: Long = System.currentTimeMillis()
    var readingStatus: String = "ALL" // Store as string for Realm compatibility

    // Reading progress fields
    var currentPage: Int = 0
    var totalPages: Int = 0
    var progressPercentage: Float = 0f
    var lastReadTimestamp: Long = 0L
}
