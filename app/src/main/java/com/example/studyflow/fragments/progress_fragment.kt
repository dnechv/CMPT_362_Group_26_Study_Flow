package com.example.studyflow.fragments

// displays progress here

// Imports
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.studyflow.R
import com.example.studyflow.database_cloud.Homework
import com.example.studyflow.repository.CoursesRepository
import com.example.studyflow.repository.HomeworkRepository
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.mapbox.maps.extension.style.expressions.dsl.generated.any

class progress_fragment : Fragment() {
    lateinit var courseNames : MutableList<String>
    private lateinit var courseIds : MutableList<String>
    private lateinit var  showBtn : Button
    private lateinit var  progressTV : TextView
    lateinit var lineChart : LineChart
    private val HWR = HomeworkRepository()
    private val CR = CoursesRepository()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //INFLATE layout
        val view = inflater.inflate(R.layout.progress_fragment, container, false)

        // items from the layout
        //val anyChartView: AnyChartView = view.findViewById(R.id.any_chart_view)
        val courseAutoC = view.findViewById<AutoCompleteTextView>(R.id.InputTypeAutoC)
        showBtn = view.findViewById(R.id.showProgressBtn)
        progressTV = view.findViewById(R.id.progressTV)
        lineChart = view.findViewById(R.id.Linechart)




        courseNames = mutableListOf<String>()
        courseIds = mutableListOf<String>()



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
                setChartByCourse2(chosenCourseName,HWlist1,lineChart)

            }

        }


        // return the view
        return view
    }


    private fun setChartByCourse2(courseName: String, HWList: List<Homework>, lineChart: LineChart) {
        val dataEntries = mutableListOf<Entry>()
        val homeworkNames = mutableListOf<String>()

        // Populate the data entries for the selected course
        var i = 0
        for (hw in HWList) {
            if (hw.homeworkMark.isNotEmpty()) {
                Log.d("Chart", hw.homeworkName)
                dataEntries.add(Entry(i.toFloat(), hw.homeworkMark.toFloat()))
                homeworkNames.add(hw.homeworkName)
                i += 1
            }

        }

        if (dataEntries.isEmpty()) {
            // If no data, show a message and hide the chart
            lineChart.visibility = View.INVISIBLE
            progressTV.visibility = View.VISIBLE
            progressTV.text = "No Data Available For The Chosen Course At The Moment"
        } else {
            progressTV.setText("Your Progress in $courseName:")
            progressTV.visibility = View.VISIBLE
            // Prepare chart data
            val lineDataSet = LineDataSet(dataEntries, "")
            Log.d ("HCTN", dataEntries.toString())
            lineDataSet.setColors(ContextCompat.getColor(requireContext(), R.color.primaryColor))
            lineDataSet.setCircleColors(ContextCompat.getColor(requireContext(), R.color.accentColor))
            lineDataSet.valueTextSize = 12f
            lineDataSet.circleRadius = 5f
            lineDataSet.setDrawValues(true)
            lineDataSet.setDrawCircles(true)


            // Create a LineData object and set it to the chart
            val lineData = LineData(lineDataSet)
            lineChart.data = lineData

            // Customize the chart appearance
            val xAxis: XAxis = lineChart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.setDrawGridLines(false)
            xAxis.textSize = 12f
            xAxis.setDrawGridLines(false)
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    // Return the corresponding homework name based on the index
                    var index = value.toInt()
                    return if (index >= 0 && index < homeworkNames.size) homeworkNames[index] else ""
                }
            }
            // Add padding to the x-axis
            xAxis.axisMinimum = -0.5f // Add padding to the left
            xAxis.axisMaximum = dataEntries.size - 0.5f // Add padding to the right


            val yAxisLeft: YAxis = lineChart.axisLeft
            yAxisLeft.setDrawGridLines(false)
            yAxisLeft.textSize = 12f
            yAxisLeft.axisMinimum = 0f // Minimum value for y-axis
            yAxisLeft.axisMaximum = 100f // Maximum value for y-axis

            val yAxisRight: YAxis = lineChart.axisRight
            yAxisRight.isEnabled = false


            lineChart.description.isEnabled = false
            lineChart.legend.isEnabled = true
            lineChart.setTouchEnabled(true)
            lineChart.setPinchZoom(true)

            // Refresh the chart
            lineChart.visibility = View.VISIBLE
            lineChart.invalidate() // Refresh the chart
        }
    }
}