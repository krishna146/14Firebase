package com.example.a14firebase.repository

import com.example.a14firebase.models.User
import com.example.a14firebase.utils.FireStoreCollection
import com.example.a14firebase.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore
) : AuthRepository {
    override fun registerUser(
        email: String,
        password: String,
        user: User,
        result: (UiState<String>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    updateUserInfo(user) { state ->
                        when (state) {
                            is UiState.Success -> {
                                result(UiState.Success("user registered successfully"))

                            }
                            is UiState.Failure -> {
                                result(UiState.Failure(state.error))
                            }
                            is UiState.Loading ->{

                            }
                        }

                    }

                } else {
                    try {
                        throw it.exception ?: java.lang.Exception("Invalid authentication")
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        result.invoke(UiState.Failure("Authentication failed, Password should be at least 6 characters"))
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        result.invoke(UiState.Failure("Authentication failed, Invalid email entered"))
                    } catch (e: FirebaseAuthUserCollisionException) {
                        result.invoke(UiState.Failure("Authentication failed, Email already registered."))
                    } catch (e: Exception) {
                        result.invoke(UiState.Failure(e.message))
                    }
                }

            }
            .addOnFailureListener {
                result(UiState.Failure(it.localizedMessage))
            }
    }

    override fun loginUser(email: String, password: String, result: (UiState<String>) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result(UiState.Success("Login successfully"))

                }
            }
            .addOnFailureListener {
                result(UiState.Failure("Authentication failed , check email and password"))

            }
    }

    override fun forgotPassword(user: User, result: (UiState<String>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun updateUserInfo(user: User, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreCollection.USER).document()
        user.id = document.id
        document
            .set(user)
            .addOnSuccessListener {
                //callback function
                result(UiState.Success("user has been update successfully"))
            }
            .addOnFailureListener {
                //callback function
                result(UiState.Failure(it.localizedMessage.toString()))
            }
    }
}