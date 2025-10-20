package com.example.supplelab.presentation.screens.home.products_overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supplelab.domain.model.Product
import com.example.supplelab.domain.repository.ProductRepository
import com.example.supplelab.util.RequestState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ProductOverviewState(
    val newProducts: List<Product> = emptyList(),
    val discountedProducts: List<Product> = emptyList()
)

class ProductOverviewViewModel(
    private val productRepository: ProductRepository

) : ViewModel() {

    val products = combine(
        productRepository.readNewProducts(),
        productRepository.readDiscountedProducts()
    ) { new, discounted ->
        when {
            new.isSuccess() && discounted.isSuccess() -> {
                val allProducts = (new.getSuccessData() + discounted.getSuccessData())
                    .distinctBy { it.id }
                
                // Pre-compute filtered and sorted lists to avoid recomposition work
                val newProductsList = allProducts
                    .filter { it.isNew }
                    .sortedBy { it.createdAt }
                    .take(5)
                
                val discountedProductsList = allProducts
                    .filter { it.isDiscounted }
                    .sortedBy { it.createdAt }
                    .take(3)
                
                RequestState.Success(
                    ProductOverviewState(
                        newProducts = newProductsList,
                        discountedProducts = discountedProductsList
                    )
                )
            }
            new.isError() -> RequestState.Error(new.getErrorMessage())
            discounted.isError() -> RequestState.Error(discounted.getErrorMessage())
            else -> RequestState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RequestState.Loading
    )

}