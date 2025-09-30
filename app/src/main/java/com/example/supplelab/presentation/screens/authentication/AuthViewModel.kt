package com.example.supplelab.presentation.screens.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supplelab.domain.repository.CustomerRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(
    private val customerRepository: CustomerRepository
): ViewModel() {
    fun createCustomer(
        user: FirebaseUser?,
        onSuccess: () -> Unit,
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
}