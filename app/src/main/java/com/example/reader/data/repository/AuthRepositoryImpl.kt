package com.example.reader.data.repository

import com.example.reader.data.mapper.UserMapper
import com.example.reader.data.source.remote.firebase.FirebaseAuthDataSource
import com.example.reader.domain.error.toAppError
import com.example.reader.domain.model.User
import com.example.reader.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository using Firebase Authentication.
 *
 * @property firebaseAuthDataSource Firebase auth data source
 * @property userMapper Mapper to convert Firebase users to domain users
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    private val userMapper: UserMapper
) : AuthRepository {
    
    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val firebaseUser = firebaseAuthDataSource.signInWithEmail(email, password)
            Result.success(userMapper.toDomain(firebaseUser))
        } catch (e: Exception) {
            Result.failure(e.toAppError())
        }
    }
    
    override suspend fun signUp(email: String, password: String, displayName: String): Result<User> {
        return try {
            val firebaseUser = firebaseAuthDataSource.signUpWithEmail(email, password, displayName)
            Result.success(userMapper.toDomain(firebaseUser))
        } catch (e: Exception) {
            Result.failure(e.toAppError())
        }
    }
    
    override suspend fun signOut(): Result<Unit> {
        return try {
            firebaseAuthDataSource.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e.toAppError())
        }
    }
    
    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            firebaseAuthDataSource.sendPasswordResetEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e.toAppError())
        }
    }
    
    override fun getCurrentUser(): Flow<User?> {
        return firebaseAuthDataSource.observeCurrentUser()
            .map { firebaseUser ->
                firebaseUser?.let { userMapper.toDomain(it) }
            }
    }
    
    override fun isUserLoggedIn(): Boolean {
        return firebaseAuthDataSource.isUserLoggedIn()
    }
}
