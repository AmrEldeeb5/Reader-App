package com.example.reader.di

import com.example.reader.data.realm.RealmRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.Realm
import javax.inject.Singleton

/**
 * Hilt module providing legacy dependencies.
 *
 * This module provides dependencies that haven't been fully refactored yet,
 * such as the old RealmRepository used by FeedbackViewModel.
 */
@Module
@InstallIn(SingletonComponent::class)
object LegacyModule {
    
    @Provides
    @Singleton
    fun provideRealmRepository(realm: Realm): RealmRepository {
        return RealmRepository(realm)
    }
}
