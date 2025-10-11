package com.example.reader.di

import com.example.reader.utils.UserPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    // UserPreferences singleton - needs Android context
    single { UserPreferences(androidContext()) }
}

val AppModule = listOf(
    appModule,
    viewModelModule
)