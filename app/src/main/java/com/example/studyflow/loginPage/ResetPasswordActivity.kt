package com.example.studyflow.loginPage

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.studyflow.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reset_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val signupButton = findViewById<Button>(R.id.btnResetPass)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmailResetPass)

        auth = FirebaseAuth.getInstance()

        signupButton.setOnClickListener {
            val email = etEmail.text.toString()
            if (email == "") {
                etEmail.setHintTextColor(Color.RED)
                Toast.makeText(this,"Enter Your Email", Toast.LENGTH_SHORT).show()
            }
            else if (email.isNotEmpty()) {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Reset password success, update UI with the signed-in user's information
                            Log.d("ResetPassword", "ResetPasswordWithEmail:success")
                            Toast.makeText(this, "Email has been sent", Toast.LENGTH_SHORT).show()
                            finish()
                            //val user = auth.currentUser

                        } else {
                            // If reset password fails, display a message to the user.
                            Log.w("ResetPassword", "ResetPasswordWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()

                        }
                    }
            }
            else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }

    }
}