package com.example.studyflow.fragments

//homework fragment will allow the user track the progress of their assignments

// Imports
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studyflow.R
import com.example.studyflow.adapters.HomeworkAdapter
import com.example.studyflow.animation.HomeWorkSwipeGesture
import com.example.studyflow.database_cloud.Courses
import com.example.studyflow.database_cloud.Homework
import com.example.studyflow.repository.CoursesRepository
import com.example.studyflow.view_models.CoursesViewModel
import com.example.studyflow.view_models.HomeworkViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar
import java.util.Locale

private lateinit var homeworkViewModel : HomeworkViewModel
private lateinit var CViewModel : CoursesViewModel
private lateinit var homeworkAdapter : HomeworkAdapter
private lateinit var addHWBtn : FloatingActionButton

private var lastDeletedHW: Homework? = null
private var lastDeletedHWPosition: Int? = null



class homework_fragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.homework_fragment, container, false)

        CViewModel = ViewModelProvider(this).get(CoursesViewModel::class.java)

        homeworkViewModel = ViewModelProvider(this).get(HomeworkViewModel::class.java)


        homeworkAdapter = HomeworkAdapter(mutableListOf()) {
            //show dialog when tapped on individual hw
                homework -> showMarkDialog(homework)
        }

        //finding the recycler view from xml
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewHW)


        //setting the layout manager
        recyclerView.layoutManager = LinearLayoutManager(context)



        val swipeGesture = HomeWorkSwipeGesture(
            homeworkAdapter,

            //callback for delete
            onDeleteCallback = { deletedHW, position ->

                // Logic for handling delete

                //record deleted course
                lastDeletedHW = deletedHW

                //record position of the delted course
                lastDeletedHWPosition = position


                //update the adapter
                //homeworkAdapter.deleteHW(position)

                homeworkViewModel.deleteHomework(deletedHW)

                Log.d("CoursesFragment", "Deleted course: $deletedHW at position: $position")
            },
            onEditCallback = { courseToEdit, position ->
                // Logic for handling edit

                showEditHWDialog(courseToEdit, position)

                //showEditCourseDialog(courseToEdit, position) // Show edit dialog
                Log.d("CoursesFragment", "Editing course: $courseToEdit at position: $position")
            }
        )

        val itemTouchHelper = ItemTouchHelper(swipeGesture)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        //connecting adapter with recycler view
        recyclerView.adapter = homeworkAdapter


        addHWBtn = view.findViewById(R.id.addHomeWorkBtn)
        val CR = CoursesRepository()
        var aa : List<Courses> = listOf()
        var coursesNameList = mutableListOf<String>()
        var coursesID = mutableListOf<String>()
        CR.getCourses { courses ->
            aa = courses
            //Log.d("COURSENAMEADD" , aa[1].courseName)
            for (course in aa ) {
                Log.d("COURSENAMEADD" , course.courseName)
                coursesNameList.add(course.courseName)
                coursesID.add(course.id)
            }
        }

        Log.d("COURSENAME", coursesNameList.toString())


        addHWBtn.setOnClickListener() {
            showSelectCourse(coursesNameList.toTypedArray() , coursesID.toTypedArray())
            Log.d("COURSENAME", coursesNameList.toString())
        }

        homeworkViewModel.homework.observe(viewLifecycleOwner) {homework ->
            Log.d("HwFrag", "hw observed: $homework") // Log fetched courses
            if (!homework.isNullOrEmpty()) {
                homeworkAdapter.updateHW(homework) // This method should call notifyDataSetChanged()
            } else {
                Log.d("Hwfrag", "No Hw found.")
            }
        }

        homeworkViewModel.getHomework()

        return view
    }




    //show the dialog to select the course first
    private fun showSelectCourse(array : Array<String> , idArray: Array<String>) {
        val chooseCourseTitleDialog = LayoutInflater.from(requireContext()).inflate(R.layout.choose_course_title_dialog, null)
        val dialogView = AlertDialog.Builder(requireContext())
            .setCustomTitle(chooseCourseTitleDialog)

        dialogView.setItems(array) {_, which ->
            when(which) {
                which -> {
                    Toast.makeText(requireContext(),array[which], Toast.LENGTH_LONG).show()
                    showAddHWDialog(array[which] , idArray[which]) //passing to course name to the showAddHWDialog

                }
            }

        }
        dialogView.show()
    }

    @SuppressLint("MissingInflatedId")
    private fun showAddHWDialog(courseName : String , courseID : String) {

        //finding the xml
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.adding_homework, null)
        val addHwTitleDialog = LayoutInflater.from(requireContext()).inflate(R.layout.add_homework_title_dialog, null)
        //creating dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)//setting the view
            .setCustomTitle(addHwTitleDialog)//setting the title
            .setPositiveButton("Add", null) //setting the positive button
            .setNegativeButton("Cancel", null)//setting the negative button
            .show()
        val HWCOurseName = dialogView.findViewById<EditText>(R.id.CourseNameTV)
        val dueDateBtn = dialogView.findViewById<Button>(R.id.dueDateBtn)
        val dueTimeBtn = dialogView.findViewById<Button>(R.id.dueTimeBtn)

        HWCOurseName.setText(courseName)

        dueDateBtn.setOnClickListener{
            showDatePickerDialog { selectedDate ->
                dueDateBtn.text = selectedDate
            }
        }

        dueTimeBtn.setOnClickListener{
            showTimePickerDialog { selectedTime ->
                dueTimeBtn.text = selectedTime
            }
        }

        //setting the positive button listener
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val HWName = dialogView.findViewById<EditText>(R.id.nameET).text.toString()
            val HWDueTime = dueTimeBtn.text
            val HWDueDate = dueDateBtn.text
            val HWDesc = dialogView.findViewById<EditText>(R.id.hwDesc).text.toString()


            //check if the fields are not empty
            if (HWName.isNotEmpty() && HWDueTime.isNotEmpty() && HWDueTime.isNotEmpty()) {
                val newHW = Homework(homeworkName = HWName, homeworkDueDate = HWDueDate.toString(),homeworkDueTime = HWDueTime.toString(), courseName = courseName, homeworkDescription = HWDesc , courseId = courseID )
                homeworkViewModel.addHomework(newHW) // Add the hw
                // homeworkViewModel.getHomework() //refresh
                dialog.dismiss()
            } else {
                dialogView.findViewById<EditText>(R.id.nameET).error = "Field required"
            }
        }
    }

    private fun showEditHWDialog(hw: Homework, position: Int) {
        // Inflate the custom dialog layout
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.edit_homework_dialog, null)
        val editHwTitleDialog = LayoutInflater.from(requireContext()).inflate(R.layout.edit_homework_title_dialog, null)

        // Populate fields with the current course data
        val homeworkNameEditText = dialogView.findViewById<EditText>(R.id.nameETEdit)
        val homeworkDueDateBtn = dialogView.findViewById<Button>(R.id.dueDateBtnEdit)
        val homeworkDueTimeBtn = dialogView.findViewById<Button>(R.id.dueTimeBtnEdit)
        val homeworkMarkEditText = dialogView.findViewById<EditText>(R.id.MarkETEdit)
        val homeworkDescEditText = dialogView.findViewById<EditText>(R.id.hwDescEdit)
        val courseNameEt = dialogView.findViewById<EditText>(R.id.CourseNameTVEdit)


        homeworkNameEditText.setText(hw.homeworkName)
        homeworkDueDateBtn.setText(hw.homeworkDueDate)
        homeworkDueTimeBtn.setText(hw.homeworkDueTime)
        homeworkMarkEditText.setText(hw.homeworkMark)
        homeworkDescEditText.setText(hw.homeworkDescription)
        courseNameEt.setText(hw.courseName)

        homeworkDueDateBtn.setOnClickListener{
            showDatePickerDialog { selectedDate ->
                homeworkDueDateBtn.text = selectedDate
            }
        }

        homeworkDueTimeBtn.setOnClickListener{
            showTimePickerDialog { selectedTime ->
                homeworkDueTimeBtn.text = selectedTime
            }
        }

        // Create and show the dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setCustomTitle(editHwTitleDialog)
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                // Update the course details
                val updatedHW = hw.copy(
                    homeworkName = homeworkNameEditText.text.toString(),
                    homeworkDueDate = homeworkDueDateBtn.text.toString(),
                    homeworkDueTime = homeworkDueTimeBtn.text.toString(),
                    homeworkMark = homeworkMarkEditText.text.toString(),
                    homeworkDescription = homeworkDescEditText.text.toString()
                )

                // Notify adapter and update database
                homeworkAdapter.updateHWAtPosition(updatedHW, position)
                homeworkViewModel.updateHW(updatedHW) // Update in the database
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDatePickerDialog(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->

            //format the  date
            val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            onDateSelected(formattedDate)
        }, year, month, day).show()
    }

    private fun showTimePickerDialog(onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
            onTimeSelected(formattedTime)
        }, hour, minute, true).show()
    }

    private fun showMarkDialog(hw: Homework){
        // Inflate the custom dialog layout
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.homework_add_mark_dialog, null)
        val addHwMarkTitleDialog = LayoutInflater.from(requireContext()).inflate(R.layout.homework_add_mark_title_dialog, null)

        val homeworkNameEditText = dialogView.findViewById<EditText>(R.id.hwNameETmark)
        val homeworkMarkEditText = dialogView.findViewById<EditText>(R.id.addMarkEt)

        homeworkNameEditText.setText(hw.homeworkName)

        val dialog = AlertDialog.Builder(requireContext())
            .setCustomTitle(addHwMarkTitleDialog)
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                // Update the course details
                val updatedHW = hw.copy(
                    homeworkMark = homeworkMarkEditText.text.toString(),

                )

                // Notify adapter and update database
                homeworkViewModel.updateHW(updatedHW) // Update in the database
            }
            .setNegativeButton("Cancel", null)
            .show()


    }
}
