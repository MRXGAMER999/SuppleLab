package com.example.supplelab.data.repository

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.example.supplelab.domain.model.Product
import com.example.supplelab.domain.repository.AdminRepository
import com.example.supplelab.util.RequestState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.net.URLDecoder
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

    override suspend fun deleteImageFromStorage(
        imageUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            if (getCurrentUserId() != null) {
                val storagePath = extractFirebaseStoragePath(imageUrl)
                if (storagePath != null) {
                    val storage = Firebase.storage.reference
                    val imageRef = storage.child(storagePath)

                    withTimeout(timeMillis = 20000L) {
                        imageRef.delete().await()
                    }
                    onSuccess()
                } else {
                    onError("Invalid image URL")
                }
            } else {
                onError("User not authenticated")
            }
        } catch (e: Exception) {
            onError(e.message ?: "Failed to delete image")
        }
    }

    override fun readLastTenProduct(): Flow<RequestState<List<Product>>> = channelFlow {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                database.collection("products")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(10)
                    .snapshots()
                    .collectLatest { query ->
                        val products = query.documents.map { document ->
                            Product(
                                id = document.id,
                                title = document.getString("title") ?: "",
                                createdAt = document.getLong("createdAt") ?: 0L,
                                description = document.getString("description") ?: "",
                                thumbnail = document.getString("thumbnail") ?: "",
                                category = document.getString("category") ?: "",
                                flavors = (document.get("flavors") as? List<*>)?.mapNotNull { it as? String },
                                weight = document.getLong("weight")?.toInt(),
                                price = document.getDouble("price") ?: 0.0,
                                isPopular = document.getBoolean("isPopular") ?: false,
                                isNew = document.getBoolean("isNew") ?: false,
                                isDiscounted = document.getBoolean("isDiscounted") ?: false,
                            )
                        }
                        send(RequestState.Success(products))

                    }
            } else {
                send(RequestState.Error("User not authenticated"))
            }

        } catch (e: Exception) {
            send(RequestState.Error(e.message ?: "Errow while reading the 10 items from the database"))

        }
    }

    private fun extractFirebaseStoragePath(imageUrl: String): String? {
        return try {
            // Firebase Storage download URLs have format:
            // https://firebasestorage.googleapis.com/v0/b/bucket-name/o/path%2Fto%2Ffile.jpg?alt=media&token=...

            // Extract the path between "/o/" and "?"
            val startIndex = imageUrl.indexOf("/o/")
            val endIndex = imageUrl.indexOf("?")

            if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                val encodedPath = imageUrl.substring(startIndex + 3, endIndex)
                // Decode URL encoding (e.g., %2F -> /)
                URLDecoder.decode(encodedPath, "UTF-8")
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // Get the file extension from the URI
    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = context.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }
}