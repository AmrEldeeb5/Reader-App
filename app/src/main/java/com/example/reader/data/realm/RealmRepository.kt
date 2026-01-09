package com.example.reader.data.realm

import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Legacy repository for feedback operations.
 * 
 * This repository is kept for backward compatibility with feedback features.
 * New features should use the Clean Architecture repositories in the domain layer.
 */
@Singleton
class RealmRepository @Inject constructor(
    private val realm: Realm
) {
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

    // ========== User Profile Operations (Legacy) ==========
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

