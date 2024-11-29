package com.example.studyflow.fragments

// displays progress here

// Imports
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.example.studyflow.R
import com.example.studyflow.database_cloud.Homework
import com.example.studyflow.repository.CoursesRepository
import com.example.studyflow.repository.HomeworkRepository
import com.mapbox.maps.extension.style.expressions.dsl.generated.any

class progress_fragment : Fragment() {
    lateinit var courseNames : MutableList<String>
    private lateinit var courseIds : MutableList<String>
    private lateinit var  showBtn : Button
    private lateinit var  progressTV : TextView
    val HWR = HomeworkRepository()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //INFLATE layout
        val view = inflater.inflate(R.layout.progress_fragment, container, false)

        // items from the layout
        val anyChartView: AnyChartView = view.findViewById(R.id.any_chart_view)
        val courseAutoC = view.findViewById<AutoCompleteTextView>(R.id.InputTypeAutoC)
        showBtn = view.findViewById(R.id.showProgressBtn)
        progressTV = view.findViewById(R.id.progressTV)

        // cartesian chart
        val cartesian: Cartesian = AnyChart.line()

        //course and homework Repositories
        //val HWR = HomeworkRepository()
        val CR = CoursesRepository()

        courseNames = mutableListOf<String>()
        courseIds = mutableListOf<String>()

        val inputItems = listOf("MACM 101","CMPT 362")


        CR.getCourses { courses ->
            val courseList = courses
            for (course in courseList) {
                courseNames.add(course.courseName)
                courseIds.add(course.id)
            }
            val inpAdapter = ArrayAdapter(requireContext(),R.layout.activity_items,courseNames.toList())
            courseAutoC.setAdapter(inpAdapter)
        }



        showBtn.setOnClickListener {
            val chosenCourseName = courseAutoC.text.toString()
            val chosenCourseID = courseIds[courseNames.indexOf(chosenCourseName)]

            HWR.getSortedSpecificHomworks(chosenCourseID) {homeworks ->
                val HWlist1 = homeworks
                Log.d("HCT1" , HWlist1.toString())
                setChartByCourse(chosenCourseName, chosenCourseID , HWlist1, anyChartView)

            }
        }


        // return the view
        return view
    }


    private fun setChartByCourse (courseName : String, courseID : String , HWList : List<Homework> , anyChartView : AnyChartView){
        val dataEntries = mutableListOf<DataEntry>()
        for (hw in HWList) {
            if (hw.courseId == courseID && hw.homeworkMark.isNotEmpty()) {
                Log.d("HCT" , hw.homeworkName)
                dataEntries.add(ValueDataEntry(hw.homeworkName, hw.homeworkMark.toInt()))
            }
        }
        if (dataEntries.isEmpty()) {
            anyChartView.visibility = View.INVISIBLE
            progressTV.visibility = View.VISIBLE
            progressTV.setText("No Data Available for the chosen course at the moment")
        }
        else {
            //anyChartView.clear()
            val cartesian: Cartesian = AnyChart.line()
            //cartesian.removeAllSeries()
            progressTV.visibility = View.INVISIBLE
            anyChartView.visibility = View.VISIBLE

            //try to debug
            cartesian.title("Your progress in course $courseName")
            Log.d("HCT", dataEntries.toList().toString())


            cartesian.data(dataEntries.toList())
            anyChartView.setChart(cartesian)

        }
    }
}