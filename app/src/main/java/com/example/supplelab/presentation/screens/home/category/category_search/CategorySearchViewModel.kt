package com.example.supplelab.presentation.screens.home.category.category_search

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supplelab.domain.model.Product
import com.example.supplelab.domain.model.ProductCategory
import com.example.supplelab.domain.repository.ProductRepository
import com.example.supplelab.util.RequestState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
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
    private var _searchQuery = MutableStateFlow("")

    var searchQuery: StateFlow<String> = _searchQuery

    fun updateSearchQuery(value: String) {
        _searchQuery.value = value
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val filteredProducts = searchQuery
        .debounce(500)
        .flatMapLatest { query ->
            if(query.isBlank()) products
            else {
              if (products.value.isSuccess()){
                  flowOf(
                      RequestState.Success(
                          products.value.getSuccessData().filter {
                              it.title.lowercase().contains(query.lowercase())
                          }
                      )
                  )
              } else products
            }
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RequestState.Loading
        )

    override fun onCleared() {
        super.onCleared()
        collectJob?.cancel()
    }
}