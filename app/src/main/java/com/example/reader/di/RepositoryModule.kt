package com.example.reader.di

import com.example.reader.data.repository.AuthRepositoryImpl
import com.example.reader.data.repository.BookRepositoryImpl
import com.example.reader.data.repository.FavoritesRepositoryImpl
import com.example.reader.data.repository.UserPreferencesRepositoryImpl
import com.example.reader.domain.repository.AuthRepository
import com.example.reader.domain.repository.BookRepository
import com.example.reader.domain.repository.FavoritesRepository
import com.example.reader.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing repository implementations.
 *
 * This module binds repository interfaces to their concrete implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
    
    @Binds
    @Singleton
    abstract fun bindBookRepository(
        bookRepositoryImpl: BookRepositoryImpl
    ): BookRepository
    
    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(
        favoritesRepositoryImpl: FavoritesRepositoryImpl
    ): FavoritesRepository
    
    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        userPreferencesRepositoryImpl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository
}
