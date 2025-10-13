package com.example.supplelab.data.repository

import com.example.supplelab.domain.model.Product
import com.example.supplelab.domain.repository.AdminRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class AdminRepositoryImpl: AdminRepository {
    override fun getCurrentUserId() = Firebase.auth.currentUser?.uid

    override suspend fun createNewProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUser = getCurrentUserId()
            if (currentUser != null) {
                val firestore = Firebase.firestore
                val productCollection = firestore.collection("products")
                productCollection.document(product.id).set(product)
                onSuccess()
            } else {
                onError("User not authenticated")
            }
        } catch (e: Exception) {
            onError(e.message ?: "An unexpected error occurred")

        }
    }
}