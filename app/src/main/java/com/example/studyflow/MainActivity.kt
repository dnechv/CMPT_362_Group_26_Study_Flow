package com.example.studyflow




//main_activity - shows main screen with term - 3 tab views on the top
// 4 tabs on the bottom

//TODO - Login using firebase auth
//TODO Offline mode <-> use local storage


//imports
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.studyflow.fragments.courses_fragment
import com.example.studyflow.fragments.homework_fragment
import com.example.studyflow.fragments.progress_fragment
import com.example.studyflow.fragments.transit_fragment
import com.google.android.material.bottomnavigation.BottomNavigationView




//firebase imports
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.FirebaseApp


//bottom nav bar

//tab bar imports


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //setting up the top bar with tabs - must be called first
        setContentView(R.layout.activity_main)


        //getting the bottom nav bar from xml
        val bottomNavigationBar = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

        //default to courses
        loadFragment(courses_fragment())

        //initialize firebase
        FirebaseApp.initializeApp(this)

        //firebase database variable
       // val firebaseDataBase = FirebaseFirestore.getInstance()



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
                    loadFragment(transit_fragment())

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
        //replace the fragment container with the fragment
        transaction.replace(R.id.fragment_container, fragment)
        //add the transaction to the back stack
        transaction.addToBackStack(null)
        //commit the transaction
        transaction.commit()
    }
    }
