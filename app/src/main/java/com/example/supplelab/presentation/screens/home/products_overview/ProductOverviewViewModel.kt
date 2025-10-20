package com.example.supplelab.presentation.screens.home.products_overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supplelab.domain.repository.ProductRepository
import com.example.supplelab.util.RequestState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ProductOverviewViewModel(
    private val productRepository: ProductRepository

) : ViewModel() {

    val products = combine(
        productRepository.readNewProducts(),
        productRepository.readDiscountedProducts()
    ) {new,discounted ->
        when {
            new.isSuccess() && discounted.isSuccess() -> {
                RequestState.Success(new.getSuccessData() + discounted.getSuccessData())
            }
            new.isError() -> new
            discounted.isError() -> discounted
            else -> RequestState.Loading

        }

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RequestState.Loading
    )

}