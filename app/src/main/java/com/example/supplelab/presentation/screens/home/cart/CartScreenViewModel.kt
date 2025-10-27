package com.example.supplelab.presentation.screens.home.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supplelab.domain.model.CartItem
import com.example.supplelab.domain.model.Customer
import com.example.supplelab.domain.model.Product
import com.example.supplelab.domain.repository.CustomerRepository
import com.example.supplelab.domain.repository.ProductRepository
import com.example.supplelab.util.RequestState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class CartScreenViewModel (
    private val customerRepository: CustomerRepository,
    private val productRepository: ProductRepository
) : ViewModel() {
    val customer: Flow<RequestState<Customer>> = customerRepository.readCustomerFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val products: Flow<RequestState<List<Product>>> = customer
        .flatMapLatest { customerState: RequestState<Customer> ->
            when (customerState) {
                is RequestState.Success -> {
                    val productIds =
                        customerState.getSuccessData().cart.map { it.productId }.toSet()
                    if (productIds.isEmpty()) {
                        flowOf(RequestState.Success(emptyList()))
                    } else {
                        productRepository.readProductsByIdsFlow(productIds.toList())
                    }
                }

                is RequestState.Error -> {
                    flowOf(RequestState.Error(customerState.getErrorMessage()))
                }

                else ->
                    flowOf(RequestState.Loading)
            }
        }

    val cartItemsWithProducts: Flow<RequestState<List<Pair<CartItem, Product>>>> = combine(customer, products) { customerState: RequestState<Customer>, productsState: RequestState<List<Product>> ->
        when {
            customerState.isSuccess() && productsState.isSuccess() -> {
                val cart = customerState.getSuccessData().cart
                val products = productsState.getSuccessData()

                val result = cart.mapNotNull { cartItem ->
                    products.find { it.id == cartItem.productId }?.let { product ->
                        cartItem to product
                    }
                }

                RequestState.Success(result)
            }

            customerState.isError() -> {
                RequestState.Error(customerState.getErrorMessage())
            }
            productsState.isError() -> {
                RequestState.Error(productsState.getErrorMessage())
            }

            else -> {
                RequestState.Loading
            }

        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val totalAmountFlow = cartItemsWithProducts
        .flatMapLatest { data ->
            if (data.isSuccess()) {
                val items = data.getSuccessData()
                val cartItems = items.map { it.first }
                val products = items.map { it.second }.associateBy { it.id }

                val totalPrice = cartItems.sumOf { cartItem ->
                    val productPrice = products[cartItem.productId]?.price ?: 0.0
                    productPrice * cartItem.quantity
                }
                    flowOf(RequestState.Success(totalPrice))
                } else if (data.isError()) flowOf(RequestState.Error(data.getErrorMessage()))
                else flowOf(RequestState.Loading)
            }
    fun updateCartItemQuantity(
        id: String,
        quantity: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            customerRepository.updateCartItemQuantity(
                id = id,
                quantity = quantity,
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }
    fun removeItemFromCart(
        id: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            customerRepository.removeItemFromCart(
                id = id,
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }
}