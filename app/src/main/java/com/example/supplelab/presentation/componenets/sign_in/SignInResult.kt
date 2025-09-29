package com.example.supplelab.presentation.componenets.sign_in

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)
data class UserData(
    val uid: String,
    val username: String?,
    val photoUrl: String?
)
