package com.example.supplelab.domain.repository

import com.google.firebase.auth.FirebaseUser

interface CustomerRepository {
    suspend fun createCustomer(
        user: FirebaseUser?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
}