package com.example.studyflow.fragments


//courses fragment - will allow the user to add courses to their current term
//display courses list, term, name of the course


//imports
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.studyflow.R
import com.example.studyflow.adapters.CoursesAdapter
import com.example.studyflow.database_cloud.Courses
import com.example.studyflow.view_models.CoursesViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton




class courses_fragment :Fragment(){

    //courseViewModel + Adapter varibles
    private lateinit var coursesViewModel: CoursesViewModel
    private lateinit var coursesAdapter: CoursesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        //inflating the layout for the fragment
        val view = inflater.inflate(R.layout.courses_fragment, container, false)

        //fiding the plus buttomn from xml;
        val plusButton = view.findViewById<FloatingActionButton>(R.id.courses_fragment_add_course_button)

        //setting the button logic onlistner
        plusButton.setOnClickListener {
            showAddCourseDialog() // Show the dialog when the button is clicked
        }

        //viewmpodel -> courses
        coursesViewModel = ViewModelProvider(this).get(CoursesViewModel::class.java)

        //adapter for courses
        coursesAdapter = CoursesAdapter(mutableListOf())

        //finding the recycler view from xml
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_courses)

        //connecting with adapter
        recyclerView.adapter = coursesAdapter

        //observing the courses
        coursesViewModel.courses.observe(viewLifecycleOwner) { courses ->
            coursesAdapter.updateCourses(courses)
        }

        //getting the courses from firebase
        coursesViewModel.getCourses()

        //returning the view
        return view
    }


    //show dialogue for adding new course
    private fun showAddCourseDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.adding_course, null)

        //creating dialogue builder
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Add Course")
            .setPositiveButton("Add", null) // Set to null for custom click handling
            .setNegativeButton("Cancel", null)
            .show()

        //setting listeners
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

            //getting the course name and term from the dialogue
            val courseName = dialogView.findViewById<EditText>(R.id.edit_course_name).text.toString()
            val courseTerm = dialogView.findViewById<EditText>(R.id.edit_course_term).text.toString()

            // Check if both fields are filled out
            if (courseName.isNotEmpty() && courseTerm.isNotEmpty()) {
                // Create a new course and add it to ViewModel
                val newCourse = Courses(courseName = courseName, courseTerm = courseTerm)
                coursesViewModel.addCourse(newCourse)
                dialog.dismiss()
            } else {
                //error check
                dialogView.findViewById<EditText>(R.id.edit_course_name).error = "Field required"
                dialogView.findViewById<EditText>(R.id.edit_course_term).error = "Field required"
            }
        }
    }
}