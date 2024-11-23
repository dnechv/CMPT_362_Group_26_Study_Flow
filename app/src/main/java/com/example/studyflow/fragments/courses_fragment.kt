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


//shake to undo imports
import android.hardware.Sensor
import android.hardware.SensorManager
import android.widget.CalendarView
import android.widget.ImageButton
import com.example.studyflow.additionalClasses.ShakeDetector
import com.google.android.material.snackbar.Snackbar

class courses_fragment : Fragment() {


    //variables
    private lateinit var coursesViewModel: CoursesViewModel
    private lateinit var coursesAdapter: CoursesAdapter

    //shake to undo variables
    private lateinit var shakeDetector: ShakeDetector
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor

    //calendar view
    private lateinit var calendarView: CalendarView


    //varibales for undo functionality
    private var lastDeletedCourse: Courses? = null
    private var lastDeletedCoursePosition: Int? = null

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


        //find the calendar view
        calendarView = view.findViewById(R.id.courses_calendar)



        // Listen for date changes
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // Handle the selected date (month is 0-indexed, add 1 for the correct month)
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            Log.d("CalendarView", "Selected Date: $selectedDate")

            //TODO - Additional stuff
        }


        //find calendar toggle button
        val calendarToggleButton = view.findViewById<ImageButton>(R.id.toggle_calendar_button)


        //set up the click listener to show and hide calendar
        calendarToggleButton.setOnClickListener {
            if (calendarView.visibility == View.VISIBLE) {
                calendarView.visibility = View.GONE
            } else {
                calendarView.visibility = View.VISIBLE
            }
        }




        //shake to undo

        sensorManager = requireActivity().getSystemService(SensorManager::class.java)

        //register the shake detector -> acceloerometer
        shakeDetector = ShakeDetector {


            //restore deleted course when device is shaked
            lastDeletedCourse?.let { course ->


                lastDeletedCoursePosition?.let { position ->

                    coursesAdapter.restoreCourse(course, position)

                    // Reset the variables
                    lastDeletedCourse = null


                    lastDeletedCoursePosition = null

                    Snackbar.make(view, "Course restored", Snackbar.LENGTH_SHORT).show()
                }
            }
        }


        //get the accelerometer
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!

        //register the listener
        sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)





        //viewModel
        coursesViewModel = ViewModelProvider(this).get(CoursesViewModel::class.java)

        //setting up dapter
        coursesAdapter = CoursesAdapter(mutableListOf())

        //finding the recycler view from xml
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_courses)


        //setting the layout manager
        recyclerView.layoutManager = LinearLayoutManager(context)

        //create swipe gesture

        val swipeGesture = SwipeGesture(
            coursesAdapter,

            //callback for delete
            onDeleteCallback = { deletedCourse, position ->

                // Logic for handling delete

                //record deleted course
                lastDeletedCourse = deletedCourse

                //record position of the delted course
                lastDeletedCoursePosition = position


                //update the adapter
                coursesAdapter.deleteCourse(position)


                Log.d("CoursesFragment", "Deleted course: $deletedCourse at position: $position")
            },
            onEditCallback = { courseToEdit, position ->
                // Logic for handling edit

                showEditCourseDialog(courseToEdit, position)

                //showEditCourseDialog(courseToEdit, position) // Show edit dialog
                Log.d("CoursesFragment", "Editing course: $courseToEdit at position: $position")
            }
        )
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


        //shake to undo








        //return view
        return view
    }



    private fun deleteCourse(poisiton: Int){

        //get the deleted course
        val deletedCourse = coursesAdapter.getCourseAtPosition(poisiton)

        //update the database
        lastDeletedCourse = deletedCourse

        //update the position
        lastDeletedCoursePosition = poisiton

        //delete the course
        coursesAdapter.deleteCourse(poisiton)


        //message for the user
        Snackbar.make(requireView(), "Course deleted", Snackbar.LENGTH_LONG).show()

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


    private fun showEditCourseDialog(course: Courses, position: Int) {
        // Inflate the custom dialog layout
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.edit_course_dialog, null)

        // Populate fields with the current course data
        val courseNameEditText = dialogView.findViewById<EditText>(R.id.edit_course_name)
        val courseTermEditText = dialogView.findViewById<EditText>(R.id.edit_course_term)

        courseNameEditText.setText(course.courseName)
        courseTermEditText.setText(course.courseTerm)

        // Create and show the dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit Course")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                // Update the course details
                val updatedCourse = course.copy(
                    courseName = courseNameEditText.text.toString(),
                    courseTerm = courseTermEditText.text.toString()
                )

                // Notify adapter and update database
                coursesAdapter.updateCourseAtPosition(updatedCourse, position)
                coursesViewModel.updateCourse(updatedCourse) // Update in the database
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    //onPause

    override fun onPause() {
        super.onPause()


        //unregistering the sensor
        sensorManager.unregisterListener(shakeDetector)
    }

    //onResume


}

