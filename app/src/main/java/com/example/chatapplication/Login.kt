package com.example.chatapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var loadingSpinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null && currentUser.isEmailVerified) {
            val intent = Intent(this@Login, MainActivity::class.java)
            finish()
            startActivity(intent)
        }
        mAuth = FirebaseAuth.getInstance()
        edtEmail = findViewById(R.id.edit_email)
        edtPassword = findViewById(R.id.edit_pwd)
        btnLogin = findViewById(R.id.btn_signin)
        btnSignUp = findViewById(R.id.btn_signup)
        loadingSpinner = findViewById(R.id.loading_spinner)

        btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnLogin.visibility = View.GONE
            btnSignUp.visibility = View.GONE
            loadingSpinner.visibility = View.VISIBLE

            login(email, password)
        }
    }

    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                loadingSpinner.visibility = View.GONE
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    if (user != null && user.isEmailVerified) {
                        val intent = Intent(this@Login, MainActivity::class.java)
                        finish()
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@Login, "Please verify your email before logging in.", Toast.LENGTH_LONG).show()
                        FirebaseAuth.getInstance().signOut()
                        btnLogin.visibility = View.VISIBLE
                        btnSignUp.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(this@Login, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    btnLogin.visibility = View.VISIBLE
                    btnSignUp.visibility = View.VISIBLE
                }
            }
    }

}
