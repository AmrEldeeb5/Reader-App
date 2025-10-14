package com.example.reader.di

import com.example.reader.data.realm.RealmRepository
import com.example.reader.utils.UserPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    // UserPreferences singleton - needs Android context
    single { UserPreferences(androidContext()) }

    // RealmRepository singleton - for database operations
    single { RealmRepository() }
}

val AppModule = listOf(
    appModule,
    viewModelModule
)