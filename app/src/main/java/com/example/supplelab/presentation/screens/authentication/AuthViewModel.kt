package com.example.supplelab.presentation.screens.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supplelab.domain.repository.CustomerRepository
import com.example.supplelab.util.RequestState
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(
    private val customerRepository: CustomerRepository
): ViewModel() {
    val customer = customerRepository.readCustomerFlow()
        .stateIn(
            viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = RequestState.Loading
        )
    fun createCustomer(
        user: FirebaseUser?,
        onSuccess: (profileComplete: Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            customerRepository.createCustomer(
                user = user,
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }
    fun signOut(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ){
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                customerRepository.signOut()
            }
            if (result is RequestState.Success) {
                onSuccess()
            } else if (result is RequestState.Error) {
                onError(result.getErrorMessage())
            }
        }
    }
}