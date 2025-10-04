package com.example.reader.di

import com.example.reader.data.api.BookViewModel
import com.example.reader.screens.SignUp.SignUpScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SignUpScreenViewModel() }
    viewModel { BookViewModel() }
}