package com.example.a14firebase.repository

import android.content.SharedPreferences
import com.example.a14firebase.models.User
import com.example.a14firebase.utils.FireStoreCollection
import com.example.a14firebase.utils.SharedPrefConstants
import com.example.a14firebase.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore,
    private val appPreferences: SharedPreferences,
    private val gson: Gson
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
                    user.id = it.result.user?.uid?:""
                    updateUserInfo(user) { state ->
                        when (state) {
                            is UiState.Success -> {
                                storeSession(id = it.result.user?.uid ?: "") { user ->
                                    if (user == null) {
                                        result(UiState.Success("user registered successfully but session failed to store"))
                                    } else {
                                        result(UiState.Success("user registered SuccessFully"))
                                    }
                                }

                            }
                            is UiState.Failure -> {
                                result(UiState.Failure(state.error))
                            }
                            is UiState.Loading -> {

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
                    storeSession(id = it.result.user?.uid ?: "") { user ->
                        if (user == null) {
                            result(UiState.Failure("failed to store local session"))
                        } else {
                            result(UiState.Success("Login successfully"))

                        }
                    }

                }
            }
            .addOnFailureListener {
                result(UiState.Failure("Authentication failed , check email and password"))

            }
    }

    override fun forgotPassword(email: String, result: (UiState<String>) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.invoke(UiState.Success("Email has been sent"))

                } else {
                    result.invoke(UiState.Failure(task.exception?.message))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failure("Authentication failed, Check email"))
            }
    }

    override fun updateUserInfo(user: User, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreCollection.USER).document(user.id)
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

    override fun storeSession(id: String, result: (User?) -> Unit) {
        database.collection(FireStoreCollection.USER).document(id)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result.toObject(User::class.java)
                    appPreferences.edit()
                        .putString(SharedPrefConstants.USER_SESSION, gson.toJson(user)).apply()
                    result(user)
                } else {
                    result(null)
                }

            }
            .addOnFailureListener {
                result(null)
            }
    }

    override fun getSession(result: (User?) -> Unit) {
        val userStr = appPreferences.getString(SharedPrefConstants.USER_SESSION, null)
        if (userStr == null) {
            result(null)
        } else {
            val user = gson.fromJson(userStr, User::class.java)
            result(user)
        }
    }

    override fun logout(result: () -> Unit) {
        auth.signOut()
        appPreferences.edit().putString(SharedPrefConstants.USER_SESSION, null).apply()
        result()
    }
}