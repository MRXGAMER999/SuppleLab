package com.example.supplelab.presentation.screens.home.products_overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supplelab.domain.repository.ProductRepository
import com.example.supplelab.util.RequestState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ProductOverviewViewModel(
    private val productRepository: ProductRepository

) : ViewModel() {
    val products = productRepository.readDiscountedProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RequestState.Loading
        )
}