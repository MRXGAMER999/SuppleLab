package com.example.supplelab.presentation.screens.home.category.category_search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supplelab.domain.model.Product
import com.example.supplelab.domain.model.ProductCategory
import com.example.supplelab.domain.repository.ProductRepository
import com.example.supplelab.util.RequestState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategorySearchViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {
    private val _products = MutableStateFlow<RequestState<List<Product>>>(RequestState.Loading)
    val products: StateFlow<RequestState<List<Product>>> = _products.asStateFlow()

    private var collectJob: Job? = null

    fun loadProductsByCategory(category: ProductCategory) {
        // Cancel previous collection if any
        collectJob?.cancel()

        // Start new collection
        collectJob = viewModelScope.launch {
            productRepository.readProductsByCategoryFlow(category).collect { state ->
                _products.value = state
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        collectJob?.cancel()
    }
}