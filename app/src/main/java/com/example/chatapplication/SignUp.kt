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
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : AppCompatActivity() {
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var editName: EditText
    private lateinit var btnSignUP: Button
    private lateinit var loadingSpinner: ProgressBar
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()

        edtEmail = findViewById(R.id.edit_email)
        edtPassword = findViewById(R.id.edit_pwd)
        editName = findViewById(R.id.edit_name)
        btnSignUP = findViewById(R.id.btn_signup)
        loadingSpinner = findViewById(R.id.loading_spinner)

        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        btnSignUP.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            val name = editName.text.toString()

            if (email.isBlank() || password.isBlank() || name.isBlank()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSignUP.visibility = View.GONE
            loadingSpinner.visibility = View.VISIBLE

            signup(name, email, password)
        }
    }

    private fun signup(name: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                loadingSpinner.visibility = View.GONE
                if (task.isSuccessful) {
                    val uid = mAuth.currentUser?.uid
                    if (uid != null) {
                        addUserToFirestore(name, email, uid)
                    }
                    mAuth.currentUser?.sendEmailVerification()
                    mAuth.signOut()
                    Toast.makeText(this, "Verification email sent. Please verify before logging in.", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@SignUp, Login::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(this@SignUp, "Signup failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    btnSignUP.visibility = View.VISIBLE
                }
            }
    }



    private fun addUserToFirestore(name: String, email: String, uid: String) {
        val userMap = hashMapOf(
            "name" to name,
            "email" to email,
            "uid" to uid
        )
        firestore.collection("users").document(uid).set(userMap)
    }
}
