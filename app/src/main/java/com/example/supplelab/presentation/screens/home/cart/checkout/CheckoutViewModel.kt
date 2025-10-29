package com.example.supplelab.presentation.screens.home.cart.checkout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supplelab.domain.model.CartItem
import com.example.supplelab.domain.model.Country
import com.example.supplelab.domain.model.Customer
import com.example.supplelab.domain.model.Order
import com.example.supplelab.domain.model.PhoneNumber
import com.example.supplelab.domain.model.Product
import com.example.supplelab.domain.repository.CustomerRepository
import com.example.supplelab.domain.repository.OrderRepository
import com.example.supplelab.domain.repository.ProductRepository
import com.example.supplelab.util.RequestState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class CheckoutScreenState(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val city: String? = null,
    val postalCode: Int? = null,
    val address: String? = null,
    val country: Country = Country.Egypt,
    val phoneNumber: PhoneNumber? = null,
    val profileComplete: Boolean = false,
    val cart: List<CartItem> = emptyList()
)
class CheckoutViewModel(
    private val customerRepository: CustomerRepository,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository
): ViewModel() {
    var screenReady: RequestState<Unit> by mutableStateOf(RequestState.Loading)
    var screenState: CheckoutScreenState by mutableStateOf(CheckoutScreenState())
        private set

    private var cartProducts: List<Product> = emptyList()

    val isFormValid: Boolean
        get() = with(screenState) {
            firstName.isNotBlank() &&
                    firstName.length in 3..50 &&
                    lastName.isNotBlank() &&
                    lastName.length in 3..50 &&
                    !city.isNullOrBlank() &&
                    city.length in 3..50 &&
                    postalCode != null &&
                    postalCode.toString().length in 3..10 &&
                    !address.isNullOrBlank() &&
                    address.length in 3..50 &&
                    phoneNumber != null &&
                    phoneNumber.number.isNotBlank() &&
                    phoneNumber.number.length in 5..15
        }
    private var dataLoadJob: Job? = null

    init {
        loadCustomerData()
    }

    fun reloadData() {
        // Cancel existing job if any
        dataLoadJob?.cancel()
        loadCustomerData()
    }

    private fun loadCustomerData() {
        // Reset state to loading
        screenReady = RequestState.Loading
        screenState = CheckoutScreenState()

        dataLoadJob = viewModelScope.launch {
            customerRepository.readCustomerFlow().collectLatest { data ->
                if (data.isSuccess()) {
                    val fetchedCustomer = data.getSuccessData()
                    screenState = CheckoutScreenState(
                        id = fetchedCustomer.id,
                        firstName = fetchedCustomer.firstName,
                        lastName = fetchedCustomer.lastName,
                        email = fetchedCustomer.email,
                        city = fetchedCustomer.city,
                        postalCode = fetchedCustomer.postalCode,
                        address = fetchedCustomer.address,
                        phoneNumber = fetchedCustomer.phoneNumber,
                        country = Country.entries.firstOrNull { it.dialCode == fetchedCustomer.phoneNumber?.dialCode }
                            ?: Country.Egypt,
                        profileComplete = fetchedCustomer.profileComplete,
                        cart = fetchedCustomer.cart
                    )

                    // Fetch products for cart items
                    val productIds = fetchedCustomer.cart.map { it.productId }.toSet()
                    if (productIds.isNotEmpty()) {
                        productRepository.readProductsByIdsFlow(productIds.toList()).collectLatest { productsData ->
                            if (productsData.isSuccess()) {
                                cartProducts = productsData.getSuccessData()
                                screenReady = RequestState.Success(Unit)
                            } else if (productsData.isError()) {
                                screenReady = RequestState.Error(productsData.getErrorMessage())
                            }
                        }
                    } else {
                        cartProducts = emptyList()
                        screenReady = RequestState.Success(Unit)
                    }
                } else if (data.isError()) {
                    screenReady = RequestState.Error(data.getErrorMessage())
                }
            }
        }
    }

    fun updateFirstName(value: String) {
        screenState = (screenState.copy(firstName = value))
    }

    fun updateLastName(value: String) {
        screenState = (screenState.copy(lastName = value))
    }

    fun updateCity(value: String) {
        screenState = (screenState.copy(city = value))
    }

    fun updatePostalCode(value: Int?) {
        screenState = (screenState.copy(postalCode = value))
    }

    fun updateAddress(value: String) {
        screenState = (screenState.copy(address = value))
    }

    fun updateCountry(value: Country) {
        screenState = (screenState.copy
            (
            country = value,
            phoneNumber = screenState.phoneNumber?.copy(
                dialCode = value.dialCode
            )

        ))
    }

    fun updatePhoneNumber(value: String?) {
        screenState = screenState.copy(
            phoneNumber = PhoneNumber(
                dialCode = screenState.country.dialCode,
                number = value!!
            )
        )
    }
    fun payOnDelivery(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        updateCustomer(
            onSuccess = {
                createOrder(
                    onSuccess = onSuccess,
                    onError = onError
                )
            },
            onError = onError
        )
    }

    private fun updateCustomer(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            customerRepository.updateCustomer(
                customer = Customer(
                    id = screenState.id,
                    firstName = screenState.firstName,
                    lastName = screenState.lastName,
                    email = screenState.email,
                    city = screenState.city,
                    postalCode = screenState.postalCode,
                    address = screenState.address,
                    phoneNumber = screenState.phoneNumber,
                    profileComplete = true
                ),
                onSuccess = {
                    onSuccess()
                },
                onError = { message ->
                    onError(message)
                }
            )
        }
    }
    private fun createOrder(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            // Create a map of products by ID for quick lookup
            val productsById = cartProducts.associateBy { it.id }

            // Calculate total amount by summing (product price * quantity) for each cart item
            val totalAmount = screenState.cart.sumOf { cartItem ->
                val productPrice = productsById[cartItem.productId]?.price ?: 0.0
                productPrice * cartItem.quantity
            }

            orderRepository.createTheOrder(
                order = Order(
                    customerId = screenState.id,
                    items = screenState.cart,
                    totalAmount = totalAmount
                ),
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }
}