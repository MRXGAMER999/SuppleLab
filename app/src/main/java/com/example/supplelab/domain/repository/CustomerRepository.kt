package com.example.supplelab.domain.repository

import com.example.supplelab.util.RequestState
import com.google.firebase.auth.FirebaseUser

interface CustomerRepository {
    fun getCurrentUserId(): String?
    suspend fun createCustomer(
        user: FirebaseUser?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    suspend fun signOut(): RequestState<Unit>
}