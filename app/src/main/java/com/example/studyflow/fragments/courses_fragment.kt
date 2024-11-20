package com.example.studyflow.fragments

// imports
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studyflow.R
import com.example.studyflow.adapters.CoursesAdapter
import com.example.studyflow.animation.SwipeGesture
import com.example.studyflow.database_cloud.Courses
import com.example.studyflow.view_models.CoursesViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class courses_fragment : Fragment() {


    //variables
    private lateinit var coursesViewModel: CoursesViewModel
    private lateinit var coursesAdapter: CoursesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.courses_fragment, container, false)

        // Finding the plus button from XML
        val plusButton = view.findViewById<FloatingActionButton>(R.id.courses_fragment_add_course_button)

        // Setting the button listener to show dialog
        plusButton.setOnClickListener {
            showAddCourseDialog() // Show the dialog when the button is clicked
        }

        //viewModel
        coursesViewModel = ViewModelProvider(this).get(CoursesViewModel::class.java)

        //setting up dapter
        coursesAdapter = CoursesAdapter(mutableListOf())

        //finding the recycler view from xml
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_courses)


        //setting the layout manager
        recyclerView.layoutManager = LinearLayoutManager(context)

        //create swipe gesture
        val swipeGesture = SwipeGesture(coursesAdapter)
        val itemTouchHelper = ItemTouchHelper(swipeGesture)

        //attach swipe gesture to recycler view
        itemTouchHelper.attachToRecyclerView(recyclerView)

        //connecting adapter with recycler view
        recyclerView.adapter = coursesAdapter

        //observe changes + update the adapter
        coursesViewModel.courses.observe(viewLifecycleOwner) { courses ->
            Log.d("CoursesFragment", "Courses observed: $courses") // log fetched courses
            if (!courses.isNullOrEmpty()) {
                coursesAdapter.updateCourses(courses)
            } else {
                Log.d("CoursesFragment", "No courses found.")
            }
        }

        //getting courses from the database
        coursesViewModel.getCourses()


        //return view
        return view
    }


    //show courses dialog add
    private fun showAddCourseDialog() {

        //finding the xml
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.adding_course, null)
        //creating dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)//setting the view
            .setTitle("Add Course")//setting the title
            .setPositiveButton("Add", null) //setting the positive button
            .setNegativeButton("Cancel", null)//setting the negative button
            .show()

        //setting the positive button listener
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val courseName = dialogView.findViewById<EditText>(R.id.edit_course_name).text.toString()
            val courseTerm = dialogView.findViewById<EditText>(R.id.edit_course_term).text.toString()


            //check if the fields are not empty
            if (courseName.isNotEmpty() && courseTerm.isNotEmpty()) {
                val newCourse = Courses(courseName = courseName, courseTerm = courseTerm)
                coursesViewModel.addCourse(newCourse) // Add the course
                coursesViewModel.getCourses() //refresh
                dialog.dismiss()
            } else {
                dialogView.findViewById<EditText>(R.id.edit_course_name).error = "Field required"
                dialogView.findViewById<EditText>(R.id.edit_course_term).error = "Field required"
            }
        }
    }
}