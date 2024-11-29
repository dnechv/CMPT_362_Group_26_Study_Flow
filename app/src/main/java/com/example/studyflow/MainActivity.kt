package com.example.studyflow

// main_activity - shows main screen with term - 3 tab views on the top
// 4 tabs on the bottom

// TODO - Login using firebase auth
// TODO - FIREASE OFFLINE PERSISTENCE -> DONE

// imports
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.studyflow.fragments.courses_fragment
import com.example.studyflow.fragments.homework_fragment
import com.example.studyflow.fragments.progress_fragment
import com.example.studyflow.fragments.TransitFragment
import com.example.studyflow.loginPage.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

// firebase imports
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestoreSettings

// bottom nav bar
// tab bar imports

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth // FirebaseAuth instance for login checks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Check if the user is logged in
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // User is not logged in, redirect to LoginActivity
            Log.d("MainActivity", "No user logged in, navigating to LoginActivity")
            startActivity(Intent(this, com.example.studyflow.loginPage.LoginActivity::class.java))
            finish() // Close MainActivity to avoid returning
            return
        }

        // Setting up the top bar with tabs - must be called first
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // Getting the bottom nav bar from XML
        val bottomNavigationBar = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

        // Default to courses
        loadFragment(courses_fragment())

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Firebase offline persistence
        // Getting the Firebase settings reference
        val firebaseOfflineSettings = FirebaseFirestoreSettings.Builder()

            // Enable offline mode
            .setPersistenceEnabled(true)

            // Set cache to unlimited
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)

            // Build the settings
            .build()

        // Getting the Firebase database reference
        val firebaseDatabase = FirebaseFirestore.getInstance()

        // Applying the settings for offline persistence
        firebaseDatabase.firestoreSettings = firebaseOfflineSettings

        // Setting up the bar to switch between fragments when tapped
        bottomNavigationBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_courses -> {
                    // Switch courses fragment using loadFragment function
                    loadFragment(courses_fragment())
                    true
                }

                R.id.nav_homework -> {
                    // Switch to homework fragment using loadFragment function
                    loadFragment(homework_fragment())
                    true
                }

                R.id.nav_office_hours -> {
                    // Switch to progress fragment using loadFragment function
                    loadFragment(progress_fragment())
                    true
                }

                R.id.nav_transit -> {
                    // Switch to transit using loadFragment function
                    loadFragment(TransitFragment())
                    true
                }

                else -> false
            }
        }
    }

    // Function to load fragment in a fragment container -> takes fragment as an argument
    private fun loadFragment(fragment: Fragment) {

        // Create fragment transaction
        val transaction = supportFragmentManager.beginTransaction()

        // Set fade in/out animations
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)

        // Replace the fragment container with the fragment
        transaction.replace(R.id.fragment_container, fragment)

        // Add the transaction to the back stack
        transaction.addToBackStack(null)

        // Commit the transaction
        transaction.commit()
    }
}