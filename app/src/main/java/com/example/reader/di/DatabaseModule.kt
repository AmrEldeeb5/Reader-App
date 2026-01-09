package com.example.reader.di

import android.util.Log
import com.example.reader.data.realm.FeedbackRealm
import com.example.reader.data.realm.UserProfileRealm
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
 * and migration strategy to preserve user data.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    private const val SCHEMA_VERSION = 1L
    private const val TAG = "DatabaseModule"

    @Provides
    @Singleton
    fun provideRealmConfiguration(): RealmConfiguration {
        return RealmConfiguration.Builder(
            schema = setOf(
                FavoriteBookRealm::class,
                FeedbackRealm::class,
                UserProfileRealm::class
            )
        )
            .name("reader_app.realm")
            .schemaVersion(SCHEMA_VERSION)
            // Use automatic migration for simple schema changes
            .migration(AutomaticSchemaMigration {
                Log.d(TAG, "Schema migration completed successfully")
            })
            // Optional: Add custom migration for complex changes when needed
            // .migration(object : RealmMigration {
            //     override fun migrate(context: DynamicRealmMigrationContext) {
            //         val oldVersion = context.oldRealm.schemaVersion()
            //         val newVersion = context.newRealm.schemaVersion()
            //         Log.d(TAG, "Migrating from version $oldVersion to $newVersion")
            //         // Handle complex migrations here
            //     }
            // })
            .build()
    }
            // Optional: Add custom migration for complex changes
            // .migration(object : RealmMigration {
            //     override fun migrate(context: DynamicRealmMigrationContext) {
            //         // Handle complex migrations here
            //     }
            // })
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRealm(configuration: RealmConfiguration): Realm {
        return Realm.open(configuration)
    }
}
