package com.example.supplelab.presentation.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supplelab.domain.model.Country
import com.example.supplelab.domain.model.PhoneNumber
import com.example.supplelab.domain.repository.CustomerRepository
import com.example.supplelab.util.RequestState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileScreenState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val city: String? = null,
    val postalCode: Int? = null,
    val address: String? = null,
    val country: Country = Country.Egypt,
    val phoneNumber: PhoneNumber? = null,
)

class ProfileViewModel(
    private val customerRepository: CustomerRepository
): ViewModel() {
    private val customer = customerRepository.readCustomerFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RequestState.Loading
        )

    var screenState: RequestState<ProfileScreenState> by mutableStateOf(RequestState.Loading)
        private set

    init {
        viewModelScope.launch {
            customer.collectLatest { data ->
                if (data.isSuccess()){
                    val fetchedCustomer = data.getSuccessData()
                    screenState = RequestState.Success(
                        ProfileScreenState(
                            firstName = fetchedCustomer.firstName,
                            lastName = fetchedCustomer.lastName,
                            email = fetchedCustomer.email,
                            city = fetchedCustomer.city,
                            postalCode = fetchedCustomer.postalCode,
                            address = fetchedCustomer.address,
                            phoneNumber = fetchedCustomer.phoneNumber,
                            country = Country.entries.firstOrNull{it.dialCode == fetchedCustomer.phoneNumber?.dialCode}
                                ?: Country.Egypt
                        )
                    )
                } else if (data.isError()){
                    screenState = RequestState.Error(data.getErrorMessage())
                }
            }
        }
    }
}