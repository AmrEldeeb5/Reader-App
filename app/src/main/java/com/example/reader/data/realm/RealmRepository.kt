package com.example.reader.data.realm

import com.example.reader.data.model.Book
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RealmRepository {
    private val realm = RealmDatabase.getInstance()

    // ========== Feedback Operations ==========
    suspend fun saveFeedback(feedbackText: String, sentimentIndex: Int) {
        realm.write {
            copyToRealm(FeedbackRealm().apply {
                this.feedbackText = feedbackText
                this.sentimentIndex = sentimentIndex
                this.timestamp = System.currentTimeMillis()
            })
        }
    }

    fun getAllFeedback(): Flow<List<FeedbackRealm>> {
        return realm.query<FeedbackRealm>()
            .sort("timestamp", Sort.DESCENDING)
            .asFlow()
            .map { it.list }
    }

    suspend fun deleteFeedback(feedback: FeedbackRealm) {
        realm.write {
            val feedbackToDelete = query<FeedbackRealm>("_id == $0", feedback._id).first().find()
            feedbackToDelete?.let { delete(it) }
        }
    }

    // ========== Favorite Books Operations ==========
    suspend fun saveFavoriteBook(book: Book) {
        realm.write {
            // Check if book already exists
            val existingBook = query<FavoriteBookRealm>("id == $0", book.id).first().find()
            if (existingBook == null) {
                copyToRealm(FavoriteBookRealm().apply {
                    id = book.id
                    title = book.title
                    author = book.author
                    subtitle = book.subtitle
                    rating = book.rating
                    coverImageUrl = book.coverImageUrl
                    userRating = book.userRating
                    description = book.description
                    isFavorite = true
                    addedTimestamp = System.currentTimeMillis()
                })
            }
        }
    }

    suspend fun removeFavoriteBook(bookId: Int) {
        realm.write {
            val bookToDelete = query<FavoriteBookRealm>("id == $0", bookId).first().find()
            bookToDelete?.let { delete(it) }
        }
    }

    suspend fun updateBookRating(bookId: Int, userRating: Double) {
        realm.write {
            val book = query<FavoriteBookRealm>("id == $0", bookId).first().find()
            book?.userRating = userRating
        }
    }

    fun getAllFavoriteBooks(): Flow<List<Book>> {
        return realm.query<FavoriteBookRealm>()
            .sort("addedTimestamp", Sort.DESCENDING)
            .asFlow()
            .map { results ->
                results.list.map { realmBook ->
                    Book(
                        id = realmBook.id,
                        title = realmBook.title,
                        author = realmBook.author,
                        subtitle = realmBook.subtitle,
                        rating = realmBook.rating,
                        coverImageUrl = realmBook.coverImageUrl,
                        userRating = realmBook.userRating,
                        description = realmBook.description,
                        isFavorite = true
                    )
                }
            }
    }

    suspend fun isFavorite(bookId: Int): Boolean {
        return realm.query<FavoriteBookRealm>("id == $0", bookId).first().find() != null
    }

    // ========== User Profile Operations ==========
    suspend fun saveUserProfile(username: String, email: String) {
        realm.write {
            // Get existing profile or create new
            val existingProfile = query<UserProfileRealm>().first().find()
            if (existingProfile != null) {
                existingProfile.username = username
                existingProfile.email = email
                existingProfile.lastUpdated = System.currentTimeMillis()
            } else {
                copyToRealm(UserProfileRealm().apply {
                    this.username = username
                    this.email = email
                    this.lastUpdated = System.currentTimeMillis()
                })
            }
        }
    }

    fun getUserProfile(): Flow<UserProfileRealm?> {
        return realm.query<UserProfileRealm>()
            .asFlow()
            .map { it.list.firstOrNull() }
    }

    suspend fun getUserProfileOnce(): UserProfileRealm? {
        return realm.query<UserProfileRealm>().first().find()
    }
}

