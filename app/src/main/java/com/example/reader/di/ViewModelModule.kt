package com.example.reader.di

import com.example.reader.data.api.BookViewModel
import com.example.reader.di.ViewModelModule
import com.example.reader.screens.SignUp.SignUpScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val ViewModelModule = module {
    viewModel { SignUpScreenViewModel() }
    viewModel { BookViewModel() }

}