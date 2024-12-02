package com.example.studyflow.fragments

// imports
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import android.widget.LinearLayout
import android.widget.TextView
import com.example.studyflow.additionalClasses.ShakeDetector
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


//courses fragment contains the working logic for the courses tab

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
            // Format the selected date (e.g., 1/12/2024)
            val selectedDate = "$dayOfMonth/${month + 1}/$year"

            // Get the day of the week (e.g., Monday, Tuesday)
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())

            //get courses matching the selected date
            val coursesToday = coursesAdapter.getCourses().filter { course ->
                isCourseOnDate(course, selectedDate, dayOfWeek)
            }

            // Show a popup with the results
            if (coursesToday.isNotEmpty()) {

                showPopup("Classes Today", coursesToday)

            } else {

                showPopup("No Classes Today", emptyList())
            }
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


                Log.d("courses fragment ", "deleted course: $deletedCourse at position: $position")
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









        //return view
        return view
    }

    //show popup function -> checks if the course is on the selected date
    private fun isCourseOnDate(course: Courses, selectedDate: String, selectedDayOfWeek: String?): Boolean {


        //get the date format
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        //get the selected date
        val selectedDateParsed = sdf.parse(selectedDate)
        val courseStartDate = course.courseStartDate?.let { sdf.parse(it) }
        val courseEndDate = course.courseEndDate?.let { sdf.parse(it) }


        //check if the selected date is between the start and end date of the course
        if (selectedDateParsed != null && courseStartDate != null && courseEndDate != null) {


            //check if the selected date is between the start and end date of the course
            if (!selectedDateParsed.before(courseStartDate) && !selectedDateParsed.after(courseEndDate)) {


                //check if the selected day of the week is in the course days
                return selectedDayOfWeek != null && course.courseDays.contains(selectedDayOfWeek)
            }
        }



        return false
    }


    //pop up for courses
    private fun showPopup(title: String, coursesToday: List<Courses>) {


        // inflate xml
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialogue_courses_for_date, null)

        // get views from xml
        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialog_date_title)
        val coursesContainer = dialogView.findViewById<LinearLayout>(R.id.dialog_courses_list_container)
        val noClassesMessage = dialogView.findViewById<TextView>(R.id.dialog_no_classes_message)

        // set title
        dialogTitle.text = title

        // clear old views
        coursesContainer.removeAllViews()

        // check for courses
        if (coursesToday.isNotEmpty()) {


            // hide no classes message if courses
            noClassesMessage.visibility = View.GONE

            // go through courses
            for (course in coursesToday) {

                //create text view for each
                val courseView = TextView(requireContext())

                //set text size, color, padding
                courseView.text = buildCourseDetails(course)
                courseView.textSize = 14f
                courseView.setTextColor(resources.getColor(R.color.black, null))
                courseView.setPadding(8, 8, 8, 8)

                // Add the course view to the container
                coursesContainer.addView(courseView)
            }
        } else {

            //if no courses show message
            noClassesMessage.visibility = View.VISIBLE
        }

        //create dialogue
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // set background
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)


        //display dialogue
        dialog.show()
    }

    // Helper function to build course details
    private fun buildCourseDetails(course: Courses): String {
        return """
        ${course.courseName} (${course.courseTerm})
        Time: ${course.courseStartTime} - ${course.courseEndTime}
        Location: ${course.courseLocation}
    """.trimIndent()
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
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.adding_course, null)

        // Initialize fields in the dialog
        val courseNameEditText = dialogView.findViewById<EditText>(R.id.edit_course_name)
        val courseTermEditText = dialogView.findViewById<EditText>(R.id.edit_course_term)
        val startDateButton = dialogView.findViewById<Button>(R.id.start_date_button)
        val endDateButton = dialogView.findViewById<Button>(R.id.end_date_button)
        val startTimeButton = dialogView.findViewById<Button>(R.id.start_time_button) // Start Time
        val endTimeButton = dialogView.findViewById<Button>(R.id.end_time_button)     // End Time

        val mondayCheckBox = dialogView.findViewById<CheckBox>(R.id.checkbox_monday)
        val tuesdayCheckBox = dialogView.findViewById<CheckBox>(R.id.checkbox_tuesday)
        val wednesdayCheckBox = dialogView.findViewById<CheckBox>(R.id.checkbox_wednesday)
        val thursdayCheckBox = dialogView.findViewById<CheckBox>(R.id.checkbox_thursday)
        val fridayCheckBox = dialogView.findViewById<CheckBox>(R.id.checkbox_friday)

        var startDate: String? = null
        var endDate: String? = null
        var startTime: String? = null
        var endTime: String? = null

        // Start date button listener
        startDateButton.setOnClickListener {
            showDatePickerDialog { selectedDate ->
                startDate = selectedDate
                startDateButton.text = selectedDate
            }
        }

        // End date button listener
        endDateButton.setOnClickListener {
            showDatePickerDialog { selectedDate ->
                endDate = selectedDate
                endDateButton.text = selectedDate
            }
        }

        // Start time button listener
        startTimeButton.setOnClickListener {
            showTimePickerDialog { selectedTime ->
                startTime = selectedTime
                startTimeButton.text = selectedTime
            }
        }

        // End time button listener
        endTimeButton.setOnClickListener {
            showTimePickerDialog { selectedTime ->
                endTime = selectedTime
                endTimeButton.text = selectedTime
            }
        }

        val addCourseTitleDialog = LayoutInflater.from(requireContext()).inflate(R.layout.add_course_title_dialogue, null)


        //create dialogue
        val dialog = AlertDialog.Builder(requireContext())
            .setCustomTitle(addCourseTitleDialog)
            .setView(dialogView)
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel", null)
            .show()


        //alert dialog
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val courseName = courseNameEditText.text.toString()
            val courseTerm = courseTermEditText.text.toString()
            val selectedDays = mutableListOf<String>()



            //checboxes check loginc
            if (mondayCheckBox.isChecked) selectedDays.add("Monday")
            if (tuesdayCheckBox.isChecked) selectedDays.add("Tuesday")
            if (wednesdayCheckBox.isChecked) selectedDays.add("Wednesday")
            if (thursdayCheckBox.isChecked) selectedDays.add("Thursday")
            if (fridayCheckBox.isChecked) selectedDays.add("Friday")


            //check for data -> create course entry
            if (courseName.isNotEmpty() && courseTerm.isNotEmpty() && startDate != null && endDate != null && startTime != null && endTime != null) {
                val newCourse = Courses(
                    courseName = courseName,
                    courseTerm = courseTerm,
                    courseStartDate = startDate,
                    courseEndDate = endDate,
                    courseStartTime = startTime!!,
                    courseEndTime = endTime!!,
                    courseDays = selectedDays
                )

                // Add the course to the database
                coursesViewModel.addCourse(newCourse)
                dialog.dismiss()
            } else {


                //no data

                if (courseName.isEmpty()) courseNameEditText.error = "Field required"
                if (courseTerm.isEmpty()) courseTermEditText.error = "Field required"
                if (startDate == null) startDateButton.error = "Select start date"
                if (endDate == null) endDateButton.error = "Select end date"
                if (startTime == null) startTimeButton.error = "Select start time"
                if (endTime == null) endTimeButton.error = "Select end time"
            }
        }
    }



    //shows date picker
    private fun showDatePickerDialog(onDateSelected: (String) -> Unit) {


        //varibales for objects
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)


        //show date picker
        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->

            //format the  date
            val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"



            //onDateSelected->  pass the formatted date
            onDateSelected(formattedDate)


            //show the date picker
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

        //fields add data
        courseNameTextView.text = course.courseName
        courseTermTextView.text = course.courseTerm
        courseStartDateTextView.text = course.courseStartDate ?: "N/A"
        courseEndDateTextView.text = course.courseEndDate ?: "N/A"
        courseDaysTextView.text = course.courseDays.joinToString(", ")

        // Show the dialog

        //create dialogue
        val dialog = AlertDialog.Builder(requireContext())

            //set attitubutes of the dialogue
            .setView(dialogView)
            .create()

        //background
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Show the dialog
        dialog.show()


    }


   //time picker for time courses
   private fun showTimePickerDialog(onTimeSelected: (String) -> Unit) {


       //vals
       val calendar = Calendar.getInstance()
       val hour = calendar.get(Calendar.HOUR_OF_DAY)
       val minute = calendar.get(Calendar.MINUTE)


       //show time picker
       TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->

           //format time
           val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)


           //onTimeSelected -> pass the formatted time
           onTimeSelected(formattedTime)


           //show the time picker
       }, hour, minute, true).show()
   }




    //show edit course dialog
    private fun showEditCourseDialog(course: Courses, position: Int) {


        //inflate the dialog
        val dialogViewEditCourses = LayoutInflater.from(requireContext()).inflate(R.layout.edit_course_dialog, null)

        //get views
        val courseNameEditText = dialogViewEditCourses.findViewById<EditText>(R.id.edit_course_name)
        val courseTermEditText = dialogViewEditCourses.findViewById<EditText>(R.id.edit_course_term)
        val startDateButton = dialogViewEditCourses.findViewById<Button>(R.id.start_date_button)
        val endDateButton = dialogViewEditCourses.findViewById<Button>(R.id.end_date_button)
        val startTimeButton = dialogViewEditCourses.findViewById<Button>(R.id.start_time_button)
        val endTimeButton = dialogViewEditCourses.findViewById<Button>(R.id.end_time_button)
        val mondayCheckBox = dialogViewEditCourses.findViewById<CheckBox>(R.id.checkbox_monday)
        val tuesdayCheckBox = dialogViewEditCourses.findViewById<CheckBox>(R.id.checkbox_tuesday)
        val wednesdayCheckBox = dialogViewEditCourses.findViewById<CheckBox>(R.id.checkbox_wednesday)
        val thursdayCheckBox = dialogViewEditCourses.findViewById<CheckBox>(R.id.checkbox_thursday)
        val fridayCheckBox = dialogViewEditCourses.findViewById<CheckBox>(R.id.checkbox_friday)

        // Add existing data
        courseNameEditText.setText(course.courseName)
        courseTermEditText.setText(course.courseTerm)
        startDateButton.text = course.courseStartDate ?: "Select Start Date"
        endDateButton.text = course.courseEndDate ?: "Select End Date"
        startTimeButton.text = course.courseStartTime ?: "Select Start Time"
        endTimeButton.text = course.courseEndTime ?: "Select End Time"

        // Recurring days
        course.courseDays.forEach { day ->


            when (day) {

                //check the days

                "Monday" -> mondayCheckBox.isChecked = true
                "Tuesday" -> tuesdayCheckBox.isChecked = true
                "Wednesday" -> wednesdayCheckBox.isChecked = true
                "Thursday" -> thursdayCheckBox.isChecked = true
                "Friday" -> fridayCheckBox.isChecked = true
            }
        }


        //date and time listeners

        startDateButton.setOnClickListener {
            showDatePickerDialog { selectedDate ->
                startDateButton.text = selectedDate
            }
        }

        endDateButton.setOnClickListener {
            showDatePickerDialog { selectedDate ->
                endDateButton.text = selectedDate
            }
        }

        startTimeButton.setOnClickListener {
            showTimePickerDialog { selectedTime ->
                startTimeButton.text = selectedTime
            }
        }

        endTimeButton.setOnClickListener {
            showTimePickerDialog { selectedTime ->
                endTimeButton.text = selectedTime
            }
        }

        // Create and show the dialog

        val dialog = AlertDialog.Builder(requireContext())

            //set attributes
            .setTitle("Edit Course")
            .setView(dialogViewEditCourses)
            .setPositiveButton("Save") { _, _ ->


                //get data
                val updatedCourseName = courseNameEditText.text.toString()
                val updatedCourseTerm = courseTermEditText.text.toString()
                val updatedStartDate = startDateButton.text.toString()
                val updatedEndDate = endDateButton.text.toString()
                val updatedStartTime = startTimeButton.text.toString()
                val updatedEndTime = endTimeButton.text.toString()
                val updatedDays = mutableListOf<String>()

                //update days

                if (mondayCheckBox.isChecked) updatedDays.add("Monday")
                if (tuesdayCheckBox.isChecked) updatedDays.add("Tuesday")
                if (wednesdayCheckBox.isChecked) updatedDays.add("Wednesday")
                if (thursdayCheckBox.isChecked) updatedDays.add("Thursday")
                if (fridayCheckBox.isChecked) updatedDays.add("Friday")

                //update data
                val updatedCourse = course.copy(

                    courseName = updatedCourseName,
                    courseTerm = updatedCourseTerm,
                    courseStartDate = updatedStartDate,
                    courseEndDate = updatedEndDate,
                    courseStartTime = updatedStartTime,
                    courseEndTime = updatedEndTime,
                    courseDays = updatedDays


                )



                //update firebase
                coursesAdapter.updateCourseAtPosition(updatedCourse, position)

                //update the viewmodel
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

