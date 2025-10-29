package com.example.supplelab.data.repository

import com.example.supplelab.domain.model.Order
import com.example.supplelab.domain.repository.CustomerRepository
import com.example.supplelab.domain.repository.OrderRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class OrderRepositoryImpl(
    private val customerRepository: CustomerRepository
): OrderRepository {
    override fun getCurrentUserId() = Firebase.auth.currentUser?.uid

    override suspend fun createTheOrder(
        order: Order,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val database = Firebase.firestore
                val orderCollection = database.collection("order")
                orderCollection.document(order.id).set(order)
                customerRepository.deleteAllCartItems(
                    onSuccess = {},
                    onError = {}
                )
                onSuccess()
            } else {
                onError("User not logged in")
            }
        }
        catch (e: Exception) {
            onError("Error creating order: ${e.message}")
        }

    }
}