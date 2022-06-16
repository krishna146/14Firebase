package com.example.a14firebase.repository

import com.example.a14firebase.models.User
import com.example.a14firebase.utils.UiState

interface AuthRepository {
    fun registerUser(email: String, password: String,user : User, result: (UiState<String>) -> Unit)
    fun loginUser(email: String, password: String, result: (UiState<String>) -> Unit)
    fun forgotPassword(user: User, result: (UiState<String>) -> Unit)
    fun updateUserInfo(user: User, result: (UiState<String>) -> Unit)
}