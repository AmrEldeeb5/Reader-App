package com.example.reader.data.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

// Realm model for user feedback
class FeedbackRealm : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var feedbackText: String = ""
    var sentimentIndex: Int = 3 // 0=very bad, 1=bad, 2=good, 3=great
    var timestamp: Long = System.currentTimeMillis()
}

// Realm model for favorite books
class FavoriteBookRealm : RealmObject {
    @PrimaryKey
    var id: Int = 0
    var title: String = ""
    var author: String = ""
    var subtitle: String = ""
    var rating: Double = 0.0
    var coverImageUrl: String? = null
    var userRating: Double? = null
    var description: String? = null
    var isFavorite: Boolean = true
    var addedTimestamp: Long = System.currentTimeMillis()
}

// Realm model for user profile
class UserProfileRealm : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var username: String = ""
    var email: String = ""
    var lastUpdated: Long = System.currentTimeMillis()
}

