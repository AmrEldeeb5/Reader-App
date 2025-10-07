package com.example.reader.di

import com.example.reader.data.api.BookViewModel
import com.example.reader.screens.SignUp.SignUpScreenViewModel
import com.example.reader.screens.explore.ExploreViewModel
import com.example.reader.screens.home.HomeViewModel
import com.example.reader.screens.login.LoginScreenViewModel
import com.example.reader.screens.saved.FavoritesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SignUpScreenViewModel() }
    viewModel { BookViewModel() }
    viewModel { LoginScreenViewModel() }
    viewModel { ExploreViewModel() }
    viewModel { HomeViewModel() }
    // Singleton for favorites - shared across all screens
    single { FavoritesViewModel() }

}