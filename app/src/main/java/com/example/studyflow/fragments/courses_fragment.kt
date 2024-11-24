package com.example.studyflow.fragments

// imports
import android.app.AlertDialog
import android.app.DatePickerDialog
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
import android.widget.Button
import android.widget.CalendarView
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import com.example.studyflow.additionalClasses.ShakeDetector
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

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
        coursesAdapter = CoursesAdapter(mutableListOf()) {

            //show dialog when tapped on individual course
            courses-> showCourseDetailsDialog(courses)
        }

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

                //remove course using adapter
                coursesAdapter.deleteCourse(position)

                //remove course from fireabase with viewmodel
                coursesViewModel.deleteCourse(deletedCourse)


                Log.d("CoursesFragment", "Deleted course: $deletedCourse at position: $position")
            },

            onEditCallback = { courseToEdit, position ->


                showEditCourseDialog(courseToEdit, position)


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
        // Finding the XML
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.adding_course, null)

        // Initialize fields in the dialog
        val courseNameEditText = dialogView.findViewById<EditText>(R.id.edit_course_name)
        val courseTermEditText = dialogView.findViewById<EditText>(R.id.edit_course_term)
        val startDateButton = dialogView.findViewById<Button>(R.id.start_date_button)
        val endDateButton = dialogView.findViewById<Button>(R.id.end_date_button)


        //checkboxes for recuring days
        val mondayCheckBox = dialogView.findViewById<CheckBox>(R.id.checkbox_monday)
        val tuesdayCheckBox = dialogView.findViewById<CheckBox>(R.id.checkbox_tuesday)
        val wednesdayCheckBox = dialogView.findViewById<CheckBox>(R.id.checkbox_wednesday)
        val thursdayCheckBox = dialogView.findViewById<CheckBox>(R.id.checkbox_thursday)
        val fridayCheckBox = dialogView.findViewById<CheckBox>(R.id.checkbox_friday)

        // Variables to store selected dates
        var startDate: String? = null
        var endDate: String? = null

        // Start date button listener
        startDateButton.setOnClickListener {
            showDatePickerDialog { selectedDate ->
                startDate = selectedDate
                startDateButton.text = selectedDate // Update button text
            }
        }

        // End date button listener
        endDateButton.setOnClickListener {
            showDatePickerDialog { selectedDate ->
                endDate = selectedDate
                endDateButton.text = selectedDate // Update button text
            }
        }

        //inflating the custom add course title
        val add_course_title_dialogue = LayoutInflater.from(requireContext()).inflate(R.layout.add_course_title_dialogue, null)

        // Create and show dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setCustomTitle(add_course_title_dialogue)
            .setView(dialogView)
            .setTitle("Add Course")
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel", null)
            .show()

        // Positive button listener
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val courseName = courseNameEditText.text.toString()
            val courseTerm = courseTermEditText.text.toString()

            //get selected repeated days
            val selectedDays = mutableListOf<String>()
            if (mondayCheckBox.isChecked) selectedDays.add("Monday")
            if (tuesdayCheckBox.isChecked) selectedDays.add("Tuesday")
            if (wednesdayCheckBox.isChecked) selectedDays.add("Wednesday")
            if (thursdayCheckBox.isChecked) selectedDays.add("Thursday")
            if (fridayCheckBox.isChecked) selectedDays.add("Friday")

            // Check if fields are filled
            if (courseName.isNotEmpty() && courseTerm.isNotEmpty() && startDate != null && endDate != null) {
                val newCourse = Courses(
                    courseName = courseName,
                    courseTerm = courseTerm,
                    courseStartDate = startDate,
                    courseEndDate = endDate,
                    courseDays = selectedDays
                )
                coursesViewModel.addCourse(newCourse) // Add the course to ViewModel
                dialog.dismiss()
            } else {
                if (courseName.isEmpty()) courseNameEditText.error = "Field required"
                if (courseTerm.isEmpty()) courseTermEditText.error = "Field required"
                if (startDate == null) startDateButton.error = "Select start date"
                if (endDate == null) {
                    endDateButton.error = "Select end date"
                }
            }
        }
    }


    private fun showDatePickerDialog(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            // Format the selected date as a string (e.g., "23/11/2024")
            val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            onDateSelected(formattedDate)
        }, year, month, day).show()
    }


    //show dialogie function when tapped on individual course
    private fun showCourseDetailsDialog(course: Courses) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.courses_dialogue_when_tapped, null)

        // Initialize views
        val courseNameTextView = dialogView.findViewById<TextView>(R.id.dialog_course_name)
        val courseTermTextView = dialogView.findViewById<TextView>(R.id.dialog_course_term)
        val courseStartDateTextView = dialogView.findViewById<TextView>(R.id.dialog_start_date)
        val courseEndDateTextView = dialogView.findViewById<TextView>(R.id.dialog_end_date)
        val courseDaysTextView = dialogView.findViewById<TextView>(R.id.dialog_course_days)

        // Populate fields
        courseNameTextView.text = course.courseName
        courseTermTextView.text = course.courseTerm
        courseStartDateTextView.text = course.courseStartDate ?: "N/A"
        courseEndDateTextView.text = course.courseEndDate ?: "N/A"
        courseDaysTextView.text = course.courseDays.joinToString(", ")

        // Show the dialog

        // Create and customize the dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Set the background of the dialog to transparent
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Show the dialog
        dialog.show()


    }


    //set dialogue background to transparent





    //shows edit courses dialoge -> allows editing firebase data
    private fun showEditCourseDialog(course: Courses, position: Int) {
        // Inflate the custom dialog layout
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.edit_course_dialog, null)

        // Initialize fields
        val courseNameEditText = dialogView.findViewById<EditText>(R.id.edit_course_name)
        val courseTermEditText = dialogView.findViewById<EditText>(R.id.edit_course_term)
        val startDateButton = dialogView.findViewById<Button>(R.id.start_date_button)
        val endDateButton = dialogView.findViewById<Button>(R.id.end_date_button)
        val mondayCheckBox = dialogView.findViewById<CheckBox>(R.id.checkbox_monday)
        val tuesdayCheckBox = dialogView.findViewById<CheckBox>(R.id.checkbox_tuesday)
        val wednesdayCheckBox = dialogView.findViewById<CheckBox>(R.id.checkbox_wednesday)
        val thursdayCheckBox = dialogView.findViewById<CheckBox>(R.id.checkbox_thursday)
        val fridayCheckBox = dialogView.findViewById<CheckBox>(R.id.checkbox_friday)

        // Populate fields with existing data
        courseNameEditText.setText(course.courseName)
        courseTermEditText.setText(course.courseTerm)
        startDateButton.text = course.courseStartDate ?: "Select Start Date"
        endDateButton.text = course.courseEndDate ?: "Select End Date"

        // Pre-check recurring days
        course.courseDays.forEach { day ->
            when (day) {
                "Mon" -> mondayCheckBox.isChecked = true
                "Tue" -> tuesdayCheckBox.isChecked = true
                "Wed" -> wednesdayCheckBox.isChecked = true
                "Thu" -> thursdayCheckBox.isChecked = true
                "Fri" -> fridayCheckBox.isChecked = true
            }
        }

        // Create and show the dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit Course")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                // Collect updated data
                val updatedCourseName = courseNameEditText.text.toString()
                val updatedCourseTerm = courseTermEditText.text.toString()
                val updatedStartDate = startDateButton.text.toString()
                val updatedEndDate = endDateButton.text.toString()
                val updatedDays = mutableListOf<String>()
                if (mondayCheckBox.isChecked) updatedDays.add("Mon")
                if (tuesdayCheckBox.isChecked) updatedDays.add("Tue")
                if (wednesdayCheckBox.isChecked) updatedDays.add("Wed")
                if (thursdayCheckBox.isChecked) updatedDays.add("Thu")
                if (fridayCheckBox.isChecked) updatedDays.add("Fri")

                // Update the course object
                val updatedCourse = course.copy(
                    courseName = updatedCourseName,
                    courseTerm = updatedCourseTerm,
                    courseStartDate = updatedStartDate,
                    courseEndDate = updatedEndDate,
                    courseDays = updatedDays
                )

                // Notify adapter and update Firestore
                coursesAdapter.updateCourseAtPosition(updatedCourse, position)
                coursesViewModel.updateCourse(updatedCourse)
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

