package com.example.reader.data.repository

import com.example.reader.data.source.local.preferences.UserPreferencesDataSource
import com.example.reader.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of UserPreferencesRepository using SharedPreferences.
 *
 * This repository manages user preferences including credentials,
 * theme settings, and username with secure storage for sensitive data.
 *
 * @property preferencesDataSource Data source for SharedPreferences operations
 */
@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val preferencesDataSource: UserPreferencesDataSource
) : UserPreferencesRepository {
    
    override suspend fun saveCredentials(email: String, password: String) {
        preferencesDataSource.saveCredentials(email, password)
    }
    
    override suspend fun clearCredentials() {
        preferencesDataSource.clearCredentials()
    }
    
    override suspend fun getRememberMe(): Boolean {
        return preferencesDataSource.getRememberMe()
    }
    
    override suspend fun setRememberMe(rememberMe: Boolean) {
        preferencesDataSource.setRememberMe(rememberMe)
    }
    
    override suspend fun getDarkTheme(): Boolean? {
        return preferencesDataSource.getDarkTheme()
    }
    
    override suspend fun setDarkTheme(isDark: Boolean) {
        preferencesDataSource.setDarkTheme(isDark)
    }
    
    override suspend fun getGreenTheme(): Boolean {
        return preferencesDataSource.getGreenTheme()
    }
    
    override suspend fun setGreenTheme(isGreen: Boolean) {
        preferencesDataSource.setGreenTheme(isGreen)
    }
    
    override fun observeUsername(): Flow<String?> {
        return preferencesDataSource.observeUsername()
    }
    
    override suspend fun updateUsername(username: String) {
        preferencesDataSource.updateUsername(username)
    }

    override suspend fun getSearchHistory(): List<String> {
        return preferencesDataSource.getSearchHistory()
    }

    override suspend fun addSearchHistory(query: String) {
        preferencesDataSource.addSearchHistory(query)
    }

    override suspend fun clearSearchHistory() {
        preferencesDataSource.clearSearchHistory()
    }

    override suspend fun removeSearchHistory(query: String) {
        preferencesDataSource.removeSearchHistory(query)
    }
}
