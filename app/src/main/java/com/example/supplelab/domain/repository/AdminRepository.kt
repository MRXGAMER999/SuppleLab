package com.example.supplelab.domain.repository

import android.net.Uri
import com.example.supplelab.domain.model.Product

interface AdminRepository {
    fun getCurrentUserId(): String?
    suspend fun createNewProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError:(String)  -> Unit
    )
    suspend fun uploadImageToStorage(uri: Uri): String?
}