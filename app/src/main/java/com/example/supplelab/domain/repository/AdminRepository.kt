package com.example.supplelab.domain.repository

import android.net.Uri
import com.example.supplelab.domain.model.Product
import com.example.supplelab.util.RequestState
import kotlinx.coroutines.flow.Flow

interface AdminRepository {
    fun getCurrentUserId(): String?
    suspend fun createNewProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError:(String)  -> Unit
    )
    suspend fun uploadImageToStorage(uri: Uri): String?

    suspend fun deleteImageFromStorage(
        imageUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    fun readLastTenProduct(): Flow<RequestState<List<Product>>>

    suspend fun readProductById(id: String): RequestState<Product>

    suspend fun updateImageThumbnail(
        productId: String,
        imageUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    suspend fun updateProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

}