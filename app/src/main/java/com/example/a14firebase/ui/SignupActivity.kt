package com.example.a14firebase.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.a14firebase.application.NoteApplication
import com.example.a14firebase.models.SignupData
import com.example.a14firebase.databinding.ActivitySignupBinding
import com.example.a14firebase.repository.NoteRepository
import com.example.a14firebase.utils.hide
import com.example.a14firebase.utils.show
import com.example.a14firebase.utils.toast
import com.example.a14firebase.viewmodel.SignupViewModel
import com.example.a14firebase.viewmodelfactory.SignupViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {
    private lateinit var activitySignupBinding: ActivitySignupBinding
    private lateinit var signupViewModel: SignupViewModel
    private lateinit var noteRepository: NoteRepository
    private val TAG = "KRISHNA"
    private lateinit var auth: FirebaseAuth

    //database reference
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //auth instance
        auth = Firebase.auth
        //database instance
        database = Firebase.database.reference
        noteRepository = (application as NoteApplication).noteRepository
        signupViewModel = ViewModelProvider(
            this,
            SignupViewModelFactory(noteRepository)
        )[SignupViewModel::class.java]
        activitySignupBinding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(activitySignupBinding.root)
        activitySignupBinding.btnRegister.setOnClickListener {
            signUp();
        }
    }

    private fun signUp() {
        val name = activitySignupBinding.etName.text.toString().trim()
        val email = activitySignupBinding.etEmail.text.toString().trim()
        val password = activitySignupBinding.etPassword.text.toString().trim()
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            toast("All fields are compulsory")
            return
        }
        //creating User
        activitySignupBinding.btnRegister.text = ""
        activitySignupBinding.btnProgressAr.show()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //saving data into realtime DB
                    saveData(name, email)
                    toast("account created successfully \n please login to go further")
                    Firebase.auth.signOut()
                    finish()
                } else {
                    // If sign in fails, display a message to the user
                    activitySignupBinding.btnProgressAr.hide()
                    activitySignupBinding.btnRegister.text = "Register"
                    toast("Creating Account failed \n please try again")

                }
            }
    }

    private fun saveData(name: String, email: String) {
        val user = SignupData(name, email)
        //getting current user id from firebase auth
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        //inserting data
        signupViewModel.saveSignUpData(user, userId)

    }


}