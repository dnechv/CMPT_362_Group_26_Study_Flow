package com.example.studyflow.loginPage

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.studyflow.MainActivity
import com.example.studyflow.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Set up edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Check if the user is already logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d("LoginActivity", "User already logged in: ${currentUser.email}")
            navigateToMainActivity() // Navigate to MainActivity
            return // Skip further execution of onCreate
        }

        // Initialize login UI elements
        setupLoginUI()
    }

    private fun setupLoginUI() {
        val etEmail = findViewById<TextInputEditText>(R.id.emailInpSignin)
        val etPassword = findViewById<TextInputEditText>(R.id.passInpSignin)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvSignup = findViewById<TextView>(R.id.tvSignup)
        val tvResetPass = findViewById<TextView>(R.id.tvRestPass)
        val errorShowTV = findViewById<TextView>(R.id.errorShowLogIn)

        // Handle login button click
        btnLogin.setOnClickListener {
            errorShowTV.text = "" // Clear any previous error messages
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty()) {
                etEmail.setHintTextColor(Color.RED)
                Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty()) {
                etPassword.setHintTextColor(Color.RED)
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
            } else {
                // Attempt login with FirebaseAuth
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Login successful, navigate to MainActivity
                            Log.d("LoginActivity", "signInWithEmail:success")
                            navigateToMainActivity()
                        } else {
                            // Login failed, display error message
                            Log.w("LoginActivity", "signInWithEmail:failure", task.exception)
                            errorShowTV.text = "Invalid username or password"
                            Toast.makeText(this, "Invalid username or password.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }


        tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        tvResetPass.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}