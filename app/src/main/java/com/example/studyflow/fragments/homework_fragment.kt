package com.example.studyflow.fragments

//homework fragment will allow the user track the progress of their assignments

// Imports
import android.annotation.SuppressLint
import android.app.AlertDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studyflow.R
import com.example.studyflow.adapters.CoursesAdapter
import com.example.studyflow.adapters.HomeworkAdapter
import com.example.studyflow.database_cloud.Courses
import com.example.studyflow.database_cloud.Homework
import com.example.studyflow.repository.CoursesRepository
import com.example.studyflow.view_models.CoursesViewModel
import com.example.studyflow.view_models.HomeworkViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

private lateinit var homeworkViewModel : HomeworkViewModel
private lateinit var CViewModel : CoursesViewModel
private lateinit var homeworkAdapter : HomeworkAdapter
private lateinit var addHWBtn : FloatingActionButton

//mikham ye list az course ha begiram, entekhab mikone , name esho migram, bad dialog badi moshakhasat, bad save

class homework_fragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.homework_fragment, container, false)

        CViewModel = ViewModelProvider(this).get(CoursesViewModel::class.java)

        homeworkViewModel = ViewModelProvider(this).get(HomeworkViewModel::class.java)


        homeworkAdapter = HomeworkAdapter(mutableListOf())

        //finding the recycler view from xml
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewHW)


        //setting the layout manager
        recyclerView.layoutManager = LinearLayoutManager(context)

        //connecting adapter with recycler view
        recyclerView.adapter = homeworkAdapter




        addHWBtn = view.findViewById(R.id.addHomeWorkBtn)
        val CR = CoursesRepository()
        var aa : List<Courses> = listOf()
        var coursesNameList = mutableListOf<String>()
        CR.getCourses { courses ->
            aa = courses
            Log.d("COURSENAMEADD" , aa[1].courseName)
            for (course in aa ) {
                Log.d("COURSENAMEADD" , course.courseName)
                coursesNameList.add(course.courseName)
            }
        }

        Log.d("COURSENAME", coursesNameList.toString())


        addHWBtn.setOnClickListener() {
            showSelectCourse(coursesNameList.toTypedArray())
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
    private fun showSelectCourse(array : Array<String>) {
        val dialogView = AlertDialog.Builder(requireContext())

        dialogView.setItems(array) {_, which ->
            when(which) {
                which -> {
                    Toast.makeText(requireContext(),array[which], Toast.LENGTH_LONG).show()
                    showAddHWDialog(array[which]) //passing to course name to the showAddHWDialog

                }
            }

        }
        dialogView.show()
    }

    @SuppressLint("MissingInflatedId")
    private fun showAddHWDialog(courseName : String) {

        //finding the xml
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.adding_homework, null)
        //creating dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)//setting the view
            .setTitle("Add Homework")//setting the title
            .setPositiveButton("Add", null) //setting the positive button
            .setNegativeButton("Cancel", null)//setting the negative button
            .show()
        val HWCOurseName = dialogView.findViewById<TextView>(R.id.CourseNameTV)
        HWCOurseName.text = courseName
        //setting the positive button listener
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val HWName = dialogView.findViewById<EditText>(R.id.nameET).text.toString()
            val HWDueTime = dialogView.findViewById<EditText>(R.id.timeET).text.toString()


            //check if the fields are not empty
            if (HWName.isNotEmpty() && HWDueTime.isNotEmpty()) {
                val newHW = Homework(homeworkName = HWName, homeworkDueTime = HWDueTime, courseName = courseName)
                homeworkViewModel.addHomework(newHW) // Add the hw
                homeworkViewModel.getHomework() //refresh
                dialog.dismiss()
            } else {
                dialogView.findViewById<EditText>(R.id.nameET).error = "Field required"
                dialogView.findViewById<EditText>(R.id.timeET).error = "Field required"
            }
        }
    }
}