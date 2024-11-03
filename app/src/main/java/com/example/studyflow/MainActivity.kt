package com.example.studyflow




//main_activity - shows main screen with term - 3 tab views on the top
// 4 tabs on the bottom

//TODO - Login using firebase auth
//


//imports
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


//bottom nav bar
import androidx.fragment.app.Fragment
import com.example.studyflow.R
import com.google.android.material.bottomnavigation.BottomNavigationView

//tab bar imports
import androidx.viewpager2.widget.ViewPager2
import com.example.studyflow.adapters.fragment_adapter
import com.example.studyflow.fragments.current_term_fragment
import com.example.studyflow.fragments.futurte_term_fragment
import com.example.studyflow.fragments.past_terms_fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.ArrayList


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //setting up the top bar with tabs
        setContentView(R.layout.activity_main)


        //setting up the top bar with tabs
        val tabLayout = findViewById<TabLayout>(R.id.menu_top_tab)
        val viewPager2 = findViewById<ViewPager2>(R.id.viewpager_tab)


        //setting up the viewpager with tabs
        //fragments
        val  fragments = ArrayList<Fragment>()


        //adding fragments to the list
        fragments.add(past_terms_fragment())
        fragments.add(current_term_fragment())
        fragments.add(futurte_term_fragment())

        //fragment adapter
        val adapter = fragment_adapter(this, fragments)
        viewPager2.adapter = adapter

        //connecting tabLayout to viewPager
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->

            //setting the title for each tab
            when (position) {
                0 -> tab.text = "Past Term"
                1 -> tab.text = "Current Term"
                2 -> tab.text = "Future Term"
            }
        }.attach() // attach finalizes the connection





        //bottom navigation bar

        //finding bottom navigation view by id
        //val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

        //TODO-when tapped switch between different fragments when tapped


        }
    }
