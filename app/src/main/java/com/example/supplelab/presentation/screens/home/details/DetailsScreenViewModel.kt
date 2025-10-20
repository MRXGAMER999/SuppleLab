package com.example.supplelab.presentation.screens.home.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supplelab.domain.model.Product
import com.example.supplelab.domain.repository.ProductRepository
import com.example.supplelab.util.RequestState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailsScreenViewModel(
    private val productRepository: ProductRepository

) : ViewModel(){
    private val _product = MutableStateFlow<RequestState<Product>>(RequestState.Loading)
    val product: StateFlow<RequestState<Product>> = _product.asStateFlow()

    private var collectJob: Job? = null

    fun loadProduct(id: String) {
        // Reset state before loading new product
        resetState()
        
        // Start new collection
        collectJob = viewModelScope.launch {
            productRepository.readProductByIdFlow(id).collect { state ->
                _product.value = state
            }
        }
    }

    fun resetState() {
        // Cancel previous collection if exists
        collectJob?.cancel()
        collectJob = null
        
        // Reset to loading state
        _product.value = RequestState.Loading
    }
}