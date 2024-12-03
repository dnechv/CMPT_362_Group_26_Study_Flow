package com.example.studyflow.fragments

// displays progress here

// Imports
import android.content.Intent
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
import com.example.studyflow.fragments.ar_activity
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


//progress_fragment contains the code for displaying the progress of the user in a line chart
//the user can select a course from the dropdown menu and view their progress in that course
//the progress is displayed in a line chart





class progress_fragment : Fragment() {


    //variables
    lateinit var courseNames : MutableList<String>
    private lateinit var courseIds : MutableList<String>
    private lateinit var  showBtn : Button
    private lateinit var  progressTV : TextView
    lateinit var lineChart : LineChart
    private val HWR = HomeworkRepository()
    private val CR = CoursesRepository()


    //to hold homework names for AR
    private var homeworkNames: MutableList<String> = mutableListOf()


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


        //FIND THE OPEN IN AR BUTTON
        val viewInARButton = view.findViewById<Button>(R.id.viewInARButton)

        //SET ON CLICK LISTENER FOR THE OPEN IN AR BUTTON

        viewInARButton.setOnClickListener {
            if (lineChart.data != null) {

                // Serialize data entries - x and y values as flota

                //creating data entries to hold the x and y values
                val dataEntries = mutableListOf<Pair<Float, Float>>()

                //iterate through all data entries
                for (i in 0 until lineChart.data.getDataSetByIndex(0).entryCount) {



                    //get the entry at index i
                    val entry = lineChart.data.getDataSetByIndex(0).getEntryForIndex(i)


                    //add the x and y values to the dataEntries list
                    dataEntries.add(Pair(entry.x, entry.y))
                }

                // get array homework data
                val homeworkNamesArray = homeworkNames.toTypedArray()

                // start ar_activity
                val intent = Intent(requireContext(), ar_activity::class.java)
                intent.putExtra("dataEntries", ArrayList(dataEntries))
                intent.putExtra("homeworkNames", homeworkNamesArray)



                startActivity(intent)


            } else {


                // Show a message if no data is available


                progressTV.text = "No Chart Data available."



                progressTV.visibility = View.VISIBLE
            }
        }




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


            //Customizing X axis of the graph

            xAxis.axisMinimum = -0.5f // padding on the left
            xAxis.axisMaximum = dataEntries.size - 0.5f // padding on the right



            // Customize Y-Axis

            //variable to hold y axis
            val yAxisLeft: YAxis = lineChart.axisLeft


            //customizing y axis
            yAxisLeft.setDrawGridLines(false)
            yAxisLeft.textSize = 12f


            //set values max and min
            yAxisLeft.axisMinimum = 0f
            yAxisLeft.axisMaximum = 100f


            // right y axis is
            val yAxisRight: YAxis = lineChart.axisRight

            //disable right y axis
            yAxisRight.isEnabled = false


            //customizing line chart
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