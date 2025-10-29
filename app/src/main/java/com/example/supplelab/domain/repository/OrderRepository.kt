package com.example.supplelab.domain.repository

import com.example.supplelab.domain.model.Order

interface OrderRepository {
    fun getCurrentUserId(): String?
    suspend fun createTheOrder(
        order: Order,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
}