package com.example.studyflow.fragments

// displays progress here

// Imports
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.example.studyflow.R

class progress_fragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //INFLATE layout
        val view = inflater.inflate(R.layout.progress_fragment, container, false)

        // get chart from xml
        val anyChartView: AnyChartView = view.findViewById(R.id.any_chart_view)

        // cartesian chart
        val cartesian: Cartesian = AnyChart.line()

        // title
        cartesian.title("Progress Over Current Term")

        // Dummy data for the chart
        val data = arrayListOf(
            ValueDataEntry("Quiz 1", 75),
            ValueDataEntry("Quiz 2", 85),
            ValueDataEntry("Quiz 3", 90),
            ValueDataEntry("Quiz 4", 80),
            ValueDataEntry("Quiz 5", 95)
        )

        // add to chart
        cartesian.data(data as List<DataEntry>?)

        //set the chart
        anyChartView.setChart(cartesian)


        // return the view
        return view
    }
}