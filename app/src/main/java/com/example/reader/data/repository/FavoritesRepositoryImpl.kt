package com.example.reader.data.repository

import com.example.reader.data.mapper.BookMapper
import com.example.reader.data.source.local.realm.RealmDataSource
import com.example.reader.domain.error.toAppError
import com.example.reader.domain.model.Book
import com.example.reader.domain.model.Favorite
import com.example.reader.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of FavoritesRepository using Realm database.
 *
 * This repository manages favorite books with immediate persistence
 * and reactive updates through Flow.
 *
 * @property realmDataSource Data source for Realm operations
 * @property bookMapper Mapper for converting between Realm entities and domain models
 */
@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val realmDataSource: RealmDataSource,
    private val bookMapper: BookMapper
) : FavoritesRepository {
    
    override suspend fun addFavorite(bookId: String, book: Book): Result<Unit> {
        return try {
            val realmEntity = bookMapper.toRealm(book)
            realmDataSource.saveFavoriteBook(realmEntity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e.toAppError())
        }
    }
    
    override suspend fun removeFavorite(bookId: String): Result<Unit> {
        return try {
            realmDataSource.removeFavoriteBook(bookId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e.toAppError())
        }
    }
    
    override suspend fun updateRating(bookId: String, rating: Double): Result<Unit> {
        return try {
            realmDataSource.updateBookRating(bookId, rating)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e.toAppError())
        }
    }
    
    override fun observeFavorites(): Flow<List<Favorite>> {
        return realmDataSource.observeAllFavoriteBooks()
            .map { realmEntities ->
                realmEntities.map { bookMapper.fromRealm(it) }
            }
    }
    
    override suspend fun isFavorite(bookId: String): Boolean {
        return try {
            realmDataSource.isFavorite(bookId)
        } catch (e: Exception) {
            false
        }
    }
}
