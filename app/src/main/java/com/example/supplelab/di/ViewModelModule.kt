package com.example.supplelab.di

import com.example.supplelab.presentation.componenets.sign_in.SignInViewModel
import com.example.supplelab.presentation.screens.admin.AdminPanelViewModel
import com.example.supplelab.presentation.screens.authentication.AuthViewModel
import com.example.supplelab.presentation.screens.admin.manageProduct.ManageProductViewModel
import com.example.supplelab.presentation.screens.home.cart.CartScreenViewModel
import com.example.supplelab.presentation.screens.home.cart.checkout.CheckoutViewModel
import com.example.supplelab.presentation.screens.home.category.category_search.CategorySearchViewModel
import com.example.supplelab.presentation.screens.home.details.DetailsScreenViewModel
import com.example.supplelab.presentation.screens.home.products_overview.ProductOverviewViewModel
import com.example.supplelab.presentation.screens.profile.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SignInViewModel() }
    viewModel { AuthViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { ManageProductViewModel(get()) }
    viewModel { AdminPanelViewModel(get()) }
    viewModel { ProductOverviewViewModel(get()) }
    viewModel { DetailsScreenViewModel(get(), get()) }
    viewModel { CartScreenViewModel(get(), get()) }
    viewModel { CategorySearchViewModel(get()) }
    viewModel { CheckoutViewModel(get(), get(), get()) }
}