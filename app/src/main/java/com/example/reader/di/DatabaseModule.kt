package com.example.reader.di

import android.util.Log
import com.example.reader.data.realm.FeedbackRealm
import com.example.reader.data.realm.UserProfileRealm
import com.example.reader.data.source.local.realm.entities.BookCacheRealm
import com.example.reader.data.source.local.realm.entities.FavoriteBookRealm
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.migration.AutomaticSchemaMigration
import javax.inject.Singleton

/**
 * Hilt module providing database-related dependencies.
 *
 * This module provides Realm database instance with proper configuration
 * including book caching support.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val SCHEMA_VERSION = 5L // Incremented for readingStatus field
    private const val TAG = "DatabaseModule"

    // Realm Configuration
    @Provides
    @Singleton
    fun provideRealmConfiguration(): RealmConfiguration {
        return RealmConfiguration.Builder(
            schema = setOf(
                FavoriteBookRealm::class,
                FeedbackRealm::class,
                UserProfileRealm::class,
                BookCacheRealm::class // Added for caching
            )
        )
            .name("reader_app.realm")
            .schemaVersion(SCHEMA_VERSION)
            .deleteRealmIfMigrationNeeded() // Delete and recreate if migration fails
            .migration(AutomaticSchemaMigration { context ->
                Log.d(TAG, "Schema migration from version ${context.oldRealm.version()} to ${context.newRealm.version()}")
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRealm(configuration: RealmConfiguration): Realm {
        return Realm.open(configuration)
    }
}

