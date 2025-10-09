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
                val customerCollection = Firebase.firestore.collection("customers")
                val customerDoc = customerCollection.document(user.uid).get().await()
                if(customerDoc.exists()){
                    // Existing user - return their profile completion status
                    val customer = customerDoc.toObject(Customer::class.java)
                    onSuccess(customer?.profileComplete ?: false)
                } else {
                    // New user - create customer with profileComplete = false
                    val customer = Customer(
                        id = user.uid,
                        firstName = user.displayName?.split(" ")?.firstOrNull() ?: "Unknown",
                        lastName = user.displayName?.split(" ")?.lastOrNull() ?: "Unknown",
                        email = user.email ?: "Unknown",
                        profileComplete = false
                    )
                    customerCollection.document(user.uid).set(customer)
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
                                send(RequestState.Success(customer))
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