package com.example.reader.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.reader.data.realm.FeedbackRealm
import com.example.reader.data.realm.UserProfileRealm
import com.example.reader.data.source.local.realm.BookCacheDataSource
import com.example.reader.data.source.local.realm.entities.BookCacheRealm
import com.example.reader.data.source.local.realm.entities.FavoriteBookRealm
import com.example.reader.utils.AppLogger
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

/**
 * Background worker to periodically clean expired cache entries.
 * Runs daily to maintain cache efficiency.
 */
class CacheCleanupWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val appLogger = AppLogger()

    override suspend fun doWork(): Result {
        return try {
            appLogger.logInfo("Starting cache cleanup")

            // Manually create Realm instance
            val realmConfig = RealmConfiguration.Builder(
                schema = setOf(
                    FavoriteBookRealm::class,
                    FeedbackRealm::class,
                    UserProfileRealm::class,
                    BookCacheRealm::class
                )
            )
                .name("reader_app.realm")
                .schemaVersion(2L)
                .build()

            val realm = Realm.open(realmConfig)
            val bookCacheDataSource = BookCacheDataSource(realm)

            val deletedCount = bookCacheDataSource.clearExpiredCache()

            realm.close()

            appLogger.logInfo("Cache cleanup completed: Removed $deletedCount expired entries")
            Result.success()
        } catch (e: Exception) {
            appLogger.logError("Cache cleanup failed", e)
            Result.retry()
        }
    }
}

