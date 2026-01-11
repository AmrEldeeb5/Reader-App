package com.example.reader.data.repository

import com.example.reader.domain.model.Book
import com.example.reader.domain.model.ReadingStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for syncing user data with Firebase Firestore.
 * Handles favorites, reading progress, and user preferences.
 */
@Singleton
class FirebaseSyncRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val FAVORITES_COLLECTION = "favorites"
        private const val READING_PROGRESS_COLLECTION = "reading_progress"
        private const val USER_PREFERENCES_COLLECTION = "preferences"
    }

    /**
     * Get current user ID. Returns null if user is not authenticated.
     */
    private fun getCurrentUserId(): String? = auth.currentUser?.uid

    /**
     * Check if user is signed in.
     */
    fun isUserSignedIn(): Boolean = auth.currentUser != null

    // ==================== FAVORITES SYNC ====================

    /**
     * Sync favorite book to Firebase.
     */
    suspend fun syncFavoriteToCloud(
        bookId: String,
        book: Book,
        readingStatus: ReadingStatus,
        userRating: Double?,
        addedTimestamp: Long
    ): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not signed in"))

            val favoriteData = hashMapOf(
                "bookId" to bookId,
                "title" to book.title,
                "author" to book.author,
                "subtitle" to book.subtitle,
                "coverImageUrl" to book.coverImageUrl,
                "description" to book.description,
                "rating" to book.rating,
                "publishedDate" to book.publishedDate,
                "readingStatus" to readingStatus.name,
                "userRating" to userRating,
                "addedTimestamp" to addedTimestamp,
                "lastUpdated" to System.currentTimeMillis()
            )

            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(FAVORITES_COLLECTION)
                .document(bookId)
                .set(favoriteData, SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Remove favorite from Firebase.
     */
    suspend fun removeFavoriteFromCloud(bookId: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not signed in"))

            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(FAVORITES_COLLECTION)
                .document(bookId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get all favorites from Firebase as a Flow.
     */
    fun observeFavoritesFromCloud(): Flow<Result<List<CloudFavorite>>> = callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            trySend(Result.failure(Exception("User not signed in")))
            close()
            return@callbackFlow
        }

        val listenerRegistration = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(FAVORITES_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val favorites = snapshot.documents.mapNotNull { doc ->
                        try {
                            CloudFavorite(
                                bookId = doc.getString("bookId") ?: return@mapNotNull null,
                                title = doc.getString("title") ?: "",
                                author = doc.getString("author") ?: "",
                                subtitle = doc.getString("subtitle") ?: "",
                                coverImageUrl = doc.getString("coverImageUrl") ?: "",
                                description = doc.getString("description"),
                                rating = doc.getDouble("rating") ?: 0.0,
                                publishedDate = doc.getString("publishedDate"),
                                readingStatus = try {
                                    ReadingStatus.valueOf(
                                        doc.getString("readingStatus") ?: ReadingStatus.ALL.name
                                    )
                                } catch (e: Exception) {
                                    ReadingStatus.ALL
                                },
                                userRating = doc.getDouble("userRating"),
                                addedTimestamp = doc.getLong("addedTimestamp") ?: 0L,
                                lastUpdated = doc.getLong("lastUpdated") ?: 0L
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(Result.success(favorites))
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    /**
     * Fetch favorites once (not real-time).
     */
    suspend fun fetchFavoritesFromCloud(): Result<List<CloudFavorite>> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not signed in"))

            val snapshot = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(FAVORITES_COLLECTION)
                .get()
                .await()

            val favorites = snapshot.documents.mapNotNull { doc ->
                try {
                    CloudFavorite(
                        bookId = doc.getString("bookId") ?: return@mapNotNull null,
                        title = doc.getString("title") ?: "",
                        author = doc.getString("author") ?: "",
                        subtitle = doc.getString("subtitle") ?: "",
                        coverImageUrl = doc.getString("coverImageUrl") ?: "",
                        description = doc.getString("description"),
                        rating = doc.getDouble("rating") ?: 0.0,
                        publishedDate = doc.getString("publishedDate"),
                        readingStatus = try {
                            ReadingStatus.valueOf(
                                doc.getString("readingStatus") ?: ReadingStatus.ALL.name
                            )
                        } catch (e: Exception) {
                            ReadingStatus.ALL
                        },
                        userRating = doc.getDouble("userRating"),
                        addedTimestamp = doc.getLong("addedTimestamp") ?: 0L,
                        lastUpdated = doc.getLong("lastUpdated") ?: 0L
                    )
                } catch (e: Exception) {
                    null
                }
            }

            Result.success(favorites)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== READING PROGRESS SYNC ====================

    /**
     * Update reading progress for a book.
     */
    suspend fun updateReadingProgress(
        bookId: String,
        currentPage: Int,
        totalPages: Int,
        lastReadTimestamp: Long
    ): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not signed in"))

            val progressData = hashMapOf(
                "bookId" to bookId,
                "currentPage" to currentPage,
                "totalPages" to totalPages,
                "progress" to if (totalPages > 0) currentPage.toDouble() / totalPages else 0.0,
                "lastReadTimestamp" to lastReadTimestamp,
                "lastUpdated" to System.currentTimeMillis()
            )

            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(READING_PROGRESS_COLLECTION)
                .document(bookId)
                .set(progressData, SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get reading progress for a book.
     */
    suspend fun getReadingProgress(bookId: String): Result<ReadingProgress?> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not signed in"))

            val doc = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(READING_PROGRESS_COLLECTION)
                .document(bookId)
                .get()
                .await()

            if (doc.exists()) {
                val progress = ReadingProgress(
                    bookId = doc.getString("bookId") ?: bookId,
                    currentPage = doc.getLong("currentPage")?.toInt() ?: 0,
                    totalPages = doc.getLong("totalPages")?.toInt() ?: 0,
                    progress = doc.getDouble("progress") ?: 0.0,
                    lastReadTimestamp = doc.getLong("lastReadTimestamp") ?: 0L
                )
                Result.success(progress)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== USER PREFERENCES SYNC ====================

    /**
     * Save user preferences to cloud.
     */
    suspend fun saveUserPreferences(
        isDarkTheme: Boolean,
        isGreenTheme: Boolean,
        fontSize: Int = 16,
        readingGoal: Int = 0
    ): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not signed in"))

            val preferencesData = hashMapOf(
                "isDarkTheme" to isDarkTheme,
                "isGreenTheme" to isGreenTheme,
                "fontSize" to fontSize,
                "readingGoal" to readingGoal,
                "lastUpdated" to System.currentTimeMillis()
            )

            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(USER_PREFERENCES_COLLECTION)
                .document("settings")
                .set(preferencesData, SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get user preferences from cloud.
     */
    suspend fun getUserPreferences(): Result<UserPreferences?> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not signed in"))

            val doc = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(USER_PREFERENCES_COLLECTION)
                .document("settings")
                .get()
                .await()

            if (doc.exists()) {
                val prefs = UserPreferences(
                    isDarkTheme = doc.getBoolean("isDarkTheme") ?: false,
                    isGreenTheme = doc.getBoolean("isGreenTheme") ?: true,
                    fontSize = doc.getLong("fontSize")?.toInt() ?: 16,
                    readingGoal = doc.getLong("readingGoal")?.toInt() ?: 0
                )
                Result.success(prefs)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== SYNC ALL DATA ====================

    /**
     * Sync all local favorites to cloud.
     * Call this when user logs in or enables sync.
     */
    suspend fun syncAllFavoritesToCloud(favorites: List<CloudFavorite>): Result<Int> {
        return try {
            var syncCount = 0
            favorites.forEach { favorite ->
                val result = syncFavoriteToCloud(
                    bookId = favorite.bookId,
                    book = Book(
                        id = favorite.bookId,
                        title = favorite.title,
                        author = favorite.author,
                        subtitle = favorite.subtitle,
                        rating = favorite.rating,
                        coverImageUrl = favorite.coverImageUrl,
                        description = favorite.description,
                        publishedDate = favorite.publishedDate
                    ),
                    readingStatus = favorite.readingStatus,
                    userRating = favorite.userRating,
                    addedTimestamp = favorite.addedTimestamp
                )
                if (result.isSuccess) syncCount++
            }
            Result.success(syncCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Data class for cloud-stored favorite book.
 */
data class CloudFavorite(
    val bookId: String,
    val title: String,
    val author: String,
    val subtitle: String,
    val coverImageUrl: String,
    val description: String?,
    val rating: Double,
    val publishedDate: String?,
    val readingStatus: ReadingStatus,
    val userRating: Double?,
    val addedTimestamp: Long,
    val lastUpdated: Long
)

/**
 * Data class for reading progress.
 */
data class ReadingProgress(
    val bookId: String,
    val currentPage: Int,
    val totalPages: Int,
    val progress: Double,
    val lastReadTimestamp: Long
)

/**
 * Data class for user preferences.
 */
data class UserPreferences(
    val isDarkTheme: Boolean,
    val isGreenTheme: Boolean,
    val fontSize: Int,
    val readingGoal: Int
)

