package com.example.supplelab.data.repository

import com.example.supplelab.domain.model.Customer
import com.example.supplelab.domain.repository.CustomerRepository
import com.example.supplelab.util.RequestState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await


class CustomerRepositoryImpl: CustomerRepository {
    override fun getCurrentUserId(): String? {
        return Firebase.auth.currentUser?.uid
    }

    override suspend fun createCustomer(
        user: FirebaseUser?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            if (user != null){
                val customerCollection = Firebase.firestore.collection("customers")
                val customer = Customer(
                    id = user.uid,
                    firstName = user.displayName?.split(" ")?.firstOrNull() ?: "Unknown",
                    lastName = user.displayName?.split(" ")?.lastOrNull() ?: "Unknown",
                    email = user.email ?: "Unknown",
                )
                val customerExists = customerCollection.document(user.uid).get().await().exists()
                if(customerExists){
                    onSuccess()
                } else {
                    customerCollection.document(user.uid).set(customer)
                    onSuccess()
                }

            } else {
                onError("User is not available")
            }

        } catch (e: Exception) {
            onError(e.message ?: "Error while creating customer")
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