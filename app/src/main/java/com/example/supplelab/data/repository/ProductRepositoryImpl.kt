package com.example.supplelab.data.repository

import com.example.supplelab.domain.model.Product
import com.example.supplelab.domain.repository.ProductRepository
import com.example.supplelab.util.RequestState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class ProductRepositoryImpl: ProductRepository {
    override fun getCurrentUserId() = Firebase.auth.currentUser?.uid


    override fun readDiscountedProducts(): Flow<RequestState<List<Product>>> = channelFlow {
            try {
                val userId = getCurrentUserId()
                if (userId != null) {
                    val database = Firebase.firestore
                    database.collection("products")
                        .whereEqualTo("isDiscounted", true)

                        .snapshots()
                        .collectLatest { query ->
                            val products = query.documents.map { document ->
                                Product(
                                    id = document.id,
                                    title = document.getString("title")?.uppercase() ?: "",
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

    override fun readNewProducts(): Flow<RequestState<List<Product>>> = channelFlow {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                database.collection("products")
                    .whereEqualTo("isNew", true)

                    .snapshots()
                    .collectLatest { query ->
                        val products = query.documents.map { document ->
                            Product(
                                id = document.id,
                                title = document.getString("title")?.uppercase() ?: "",
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
}
