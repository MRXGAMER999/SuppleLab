package com.example.supplelab.di

import com.example.supplelab.presentation.componenets.sign_in.SignInViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SignInViewModel() }
}