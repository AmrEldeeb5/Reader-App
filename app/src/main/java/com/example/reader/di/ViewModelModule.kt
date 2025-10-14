package com.example.reader.di

import com.example.reader.data.api.BookViewModel
import com.example.reader.screens.SignUp.SignUpScreenViewModel
import com.example.reader.screens.details.BookDetailsViewModel
import com.example.reader.screens.explore.ExploreViewModel
import com.example.reader.screens.home.HomeViewModel
import com.example.reader.screens.login.LoginScreenViewModel
import com.example.reader.screens.profile.FeedbackViewModel
import com.example.reader.screens.profile.UserProfileViewModel
import com.example.reader.screens.saved.FavoritesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SignUpScreenViewModel() }
    viewModel { BookViewModel() }
    viewModel { LoginScreenViewModel() }
    viewModel { ExploreViewModel() }
    viewModel { HomeViewModel() }
    viewModel { BookDetailsViewModel() }
    viewModel { UserProfileViewModel(get(), get()) }
    viewModel { FeedbackViewModel(get()) }
    // Singleton for favorites - shared across all screens with Realm persistence
    single { FavoritesViewModel(get()) }
}