package com.example.reader.di

import android.content.Context
import com.example.reader.data.realm.FeedbackRealm
import com.example.reader.data.realm.UserProfileRealm
import com.example.reader.data.source.local.realm.entities.FavoriteBookRealm
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

/**
 * Hilt module providing database-related dependencies.
 *
 * This module provides Realm database instance with proper configuration.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
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
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded() // For development; use proper migration in production
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRealm(configuration: RealmConfiguration): Realm {
        return Realm.open(configuration)
    }
}
