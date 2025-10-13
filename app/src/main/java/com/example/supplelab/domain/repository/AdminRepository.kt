package com.example.supplelab.domain.repository

import com.example.supplelab.domain.model.Product

interface AdminRepository {
    fun getCurrentUserId(): String?
    suspend fun createNewProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError:(String)  -> Unit
    )
}