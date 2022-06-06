package com.example.a14firebase.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.a14firebase.databinding.ActivityLoginBinding
import com.example.a14firebase.utils.hide
import com.example.a14firebase.utils.show
import com.example.a14firebase.utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    lateinit var activityLoginBinding: ActivityLoginBinding
    private val TAG = "KRISHNA"

    //firebase auth
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //initializing auth variable
        auth = Firebase.auth
        activityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(activityLoginBinding.root)
        activityLoginBinding.btnLogin.setOnClickListener {
            login()
        }
        activityLoginBinding.txtSignUp.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login() {
        //login
        val email = activityLoginBinding.etEmail.text.toString().trim()
        val password = activityLoginBinding.etPassword.text.toString().trim()
        if(email.isEmpty()){
            toast("email field can't be empty")
            return
        }
        if(password.isEmpty())
        {
            toast("password filed can't be empty")
            return
        }
        activityLoginBinding.btnProgressAr.show()
        activityLoginBinding.btnLogin.text = ""
        //signing in
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    toast("login successful")
                    nextActivity()
                } else {
                    toast("authentication failed")
                    activityLoginBinding.btnProgressAr.hide()
                    activityLoginBinding.btnLogin.text = "login"
                }
            }

    }



    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            nextActivity();
        }
    }

    private fun nextActivity() {
        val intent = Intent(this@LoginActivity, NoteActivity::class.java)
        startActivity(intent)
        finish()

    }

}