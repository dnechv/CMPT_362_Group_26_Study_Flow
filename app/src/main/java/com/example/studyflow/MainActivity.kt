package com.example.studyflow


//main_activity - shows main screen with term - 3 tab views on the top
// 4 tabs on the bottom

//TODO - Login using firebase auth
//TODO - FIREASE OFFLINE PERSISTENCE -> DONE


//imports
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.studyflow.fragments.courses_fragment
import com.example.studyflow.fragments.homework_fragment
import com.example.studyflow.fragments.progress_fragment
import com.example.studyflow.fragments.TransitFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


//firebase imports
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestoreSettings


//bottom nav bar

//tab bar imports


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //setting up the top bar with tabs - must be called first
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }


        //getting the bottom nav bar from xml
        val bottomNavigationBar = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

        //default to courses
        loadFragment(courses_fragment())

        //initialize firebase
        FirebaseApp.initializeApp(this)

       //firebase offline persistence

        //getting the firebase settings reference
        val firebaseOfflineSettings = FirebaseFirestoreSettings.Builder()

            //enable offline mode
            .setPersistenceEnabled(true)

            //set cache to unlimited
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)

            //build the settings
            .build()


        //getting the firebase database reference
        val firebaseDatabase = FirebaseFirestore.getInstance()

        //appying the settings for offline persistence
        firebaseDatabase.firestoreSettings = firebaseOfflineSettings



        //setting up the bar to switch bewteen fragments when tapped
        bottomNavigationBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_courses -> {
                    //switch courses fragment using loadFragment function
                    loadFragment(courses_fragment())

                    true
                }

                R.id.nav_homework -> {
                    //switch to homework fragment using loadFragment function
                    loadFragment(homework_fragment())
                    true
                }

                R.id.nav_office_hours -> {
                    //switch to progress fragment using loadFragment function
                    loadFragment(progress_fragment())
                    true
                }

                R.id.nav_transit -> {
                    //switch to transit using loadFragment function
                    loadFragment(TransitFragment())

                    true
                }

                else -> false
            }
        }


    }

    //function to load fragment in a fragment container ->takes fragment as an argument
    private fun loadFragment(fragment: Fragment) {


        //create fragment transaction
        val transaction = supportFragmentManager.beginTransaction()

        // set fade in/out animations
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)

        //replace the fragment container with the fragment
        transaction.replace(R.id.fragment_container, fragment)

        //add the transaction to the back stack
        transaction.addToBackStack(null)


        //commit the transaction
        transaction.commit()
    }
}
