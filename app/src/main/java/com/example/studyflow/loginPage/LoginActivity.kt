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
import com.mapbox.maps.extension.style.expressions.dsl.generated.color

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val firebaseDatabase = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        val etEmail = findViewById<TextInputEditText>(R.id.emailInpSignin)
        val etPassword = findViewById<TextInputEditText>(R.id.passInpSignin)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvSignup = findViewById<TextView>(R.id.tvSignup)
        val errorShowTV = findViewById<TextView>(R.id.errorShowLogIn)




        btnLogin.setOnClickListener {
            errorShowTV.setText("")
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if(email==""){
                etEmail.setHintTextColor(Color.RED)
                Toast.makeText(this,"Enter Email", Toast.LENGTH_SHORT).show()
            }
            else if(password==""){
                etPassword.setHintTextColor(Color.RED)
                Toast.makeText(this,"Enter Password", Toast.LENGTH_SHORT).show()

            }

            else {

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TTT", "signInWithEmail:success")
                            val user = auth.currentUser
                            finish()

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TTT", "signInWithEmail:failure", task.exception)
                            errorShowTV.setText("Invalid username or password")
                            Toast.makeText(this,"Invalid username or password.", Toast.LENGTH_SHORT).show()

                        }
                    }

            }
        }

        tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }


}