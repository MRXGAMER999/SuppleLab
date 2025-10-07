package com.example.supplelab.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val city: String? = null,
    val postalCode: Int? = null,
    val address: String? = null,
    val phoneNumber: PhoneNumber? = null,
    val cart: List<CartItem> = emptyList()
)
@Serializable
data class PhoneNumber(
    val dialCode: Int = 0,
    val number: String = ""
)
