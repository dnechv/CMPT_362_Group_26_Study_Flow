package com.example.studyflow.loginPage

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.studyflow.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val firebaseDatabase = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val signupButton = findViewById<Button>(R.id.btnSignup)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmailSignup)
        val etPassword = findViewById<TextInputEditText>(R.id.etPasswordSignup)
        val tvLogin = findViewById<TextView>(R.id.tvLogin)
        val errorShowTV = findViewById<TextView>(R.id.errorShowSignUp)
        auth = FirebaseAuth.getInstance()



        signupButton.setOnClickListener {
            errorShowTV.setText("")
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (email == "") {
                etEmail.setHintTextColor(Color.RED)
                Toast.makeText(this,"Enter Your Email", Toast.LENGTH_SHORT).show()
            }
            else if (password.length < 6) {
                Toast.makeText(this,"Password length must contain 6 or more characters", Toast.LENGTH_SHORT).show()
                errorShowTV.setText("password must be at least 6 characters")
                etPassword.setText("")
            }
            else if (email.isNotEmpty() && password.isNotEmpty()) {
                Log.d("TTT", "createUserWithEmail:success")
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TTT", "createUserWithEmail:success")
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                            //val user = auth.currentUser

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TTT", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                            errorShowTV.setText("Invalid Email, try again")

                        }
                    }
            }
            else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

//    private fun saveUserToFirestore(userId: String, username: String, email: String) {
//        val user = mapOf(
//            "id" to userId,
//            "username" to username,
//            "email" to email
//        )
//
//        firebaseDatabase.collection("users").document(userId)
//            .set(user)
//            .addOnSuccessListener {
//                Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show()
//                startActivity(Intent(this, LoginActivity::class.java))
//                finish()
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(this, "Failed to save user: ${exception.message}", Toast.LENGTH_LONG).show()
//            }
//    }
}