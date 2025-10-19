package com.example.supplelab.data.repository

import com.example.supplelab.domain.model.Customer
import com.example.supplelab.domain.repository.CustomerRepository
import com.example.supplelab.util.RequestState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.tasks.await


class CustomerRepositoryImpl: CustomerRepository {
    override fun getCurrentUserId(): String? {
        return Firebase.auth.currentUser?.uid
    }

    override suspend fun createCustomer(
        user: FirebaseUser?,
        onSuccess: (profileComplete: Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            if (user != null){
                val database = Firebase.firestore
                val customerCollection = database.collection("customers")
                val customerDoc = customerCollection.document(user.uid).get().await()
                if(customerDoc.exists()){
                    // Existing user - return their profile completion status
                    val customer = customerDoc.toObject(Customer::class.java)
                    onSuccess(customer?.profileComplete ?: false)
                } else {
                    // New user - create customer with profileComplete = false
                    val customerData = hashMapOf(
                        "id" to user.uid,
                        "firstName" to (user.displayName?.split(" ")?.firstOrNull() ?: "Unknown"),
                        "lastName" to (user.displayName?.split(" ")?.lastOrNull() ?: "Unknown"),
                        "email" to (user.email ?: "Unknown"),
                        "profileComplete" to false,
                        "cart" to emptyList<Any>()
                    )

                    customerCollection.document(user.uid).set(customerData).await()
                    customerCollection.document(user.uid)
                        .collection("privateData")
                        .document("role")
                        .set(mapOf("isAdmin" to false))
                        .await()
                    onSuccess(false) // New user, profile not complete
                }

            } else {
                onError("User is not available")
            }

        } catch (e: Exception) {
            onError(e.message ?: "Error while creating customer")
        }
    }

    override fun readCustomerFlow(): Flow<RequestState<Customer>> = channelFlow {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                database.collection("customers")
                    .document(userId)
                    .snapshots()
                    .collectLatest { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val customer = documentSnapshot.toObject(Customer::class.java)
                            if (customer != null) {
                                // Fetch isAdmin from privateData subcollection
                                val isAdmin = try {
                                    database.collection("customers")
                                        .document(userId)
                                        .collection("privateData")
                                        .document("role")
                                        .get()
                                        .await()
                                        .getBoolean("isAdmin") ?: false
                                } catch (e: Exception) {
                                    false
                                }
                                // Merge isAdmin with customer data
                                val updatedCustomer = customer.copy(isAdmin = isAdmin)
                                send(RequestState.Success(updatedCustomer))
                            } else {
                                send(RequestState.Error("Error parsing customer data"))
                            }
                        } else {
                            send(RequestState.Error("Customer data does not exist"))
                        }
                    }
            } else {
                send(RequestState.Error("User is not available"))
            }
        } catch (e: Exception) {
            send(RequestState.Error("Error fetching customer data: ${e.message}"))
        }
    }

    override suspend fun updateCustomer(
        customer: Customer,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val firestore = Firebase.firestore
                val customerCollection = firestore.collection("customers")
                val existingCustomer = customerCollection
                    .document(customer.id)
                    .get()
                    .await()
                if(existingCustomer.exists()){
                    customerCollection
                        .document(customer.id)
                        .update(
                            "firstName", customer.firstName,
                            "lastName", customer.lastName,
                            "city", customer.city,
                            "postalCode", customer.postalCode,
                            "address", customer.address,
                            "phoneNumber", customer.phoneNumber,
                            "profileComplete", customer.profileComplete
                        )
                        .await()
                    onSuccess()
                } else {
                    onError("Customer not found")
                }
            } else {
                onError("User is not available")
            }

        } catch (e: Exception) {
            onError(e.message ?: "Error updating customer")
        }
    }


    override suspend fun signOut(): RequestState<Unit> {
        return try {
            Firebase.auth.signOut()
            RequestState.Success(Unit)
        } catch (e: Exception) {
             RequestState.Error(e.message ?: "Error signing out")
        }
    }
}