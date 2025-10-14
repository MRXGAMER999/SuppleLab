package com.example.supplelab.data.repository

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.example.supplelab.domain.model.Product
import com.example.supplelab.domain.repository.AdminRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AdminRepositoryImpl(private val context: Context): AdminRepository {
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

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun uploadImageToStorage(uri: Uri): String? {
        return if (getCurrentUserId() != null){
            val storage = Firebase.storage.reference

            // Get file extension from URI
            val extension = getFileExtension(uri) ?: "jpg" // Default to jpg if extension can't be determined
            val fileName = "${Uuid.random().toHexString()}.$extension"
            val imagePath = storage.child("images/$fileName")

            try {
                withTimeout(timeMillis = 20000L){
                    imagePath.putFile(uri).await()
                    imagePath.downloadUrl.await().toString()
                }
            } catch (e: Exception) {
                null
            }
        } else null
    }

    // Get the file extension from the URI
    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = context.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }
}