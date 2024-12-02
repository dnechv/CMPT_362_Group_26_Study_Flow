package com.example.studyflow.fragments

import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.studyflow.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter



//contains the graph code to simulate AR effect
//pulls data from the progress tab via intent -> data is in the form of a list of pairs of floats


class ar_activity : AppCompatActivity(), SensorEventListener {


    //camera permission code
    private val CAMERA_PERMISSION_CODE = 100

    //chart initalization
    private lateinit var lineChart: LineChart


    //phone tracking movemement

    //sensor for AR camera movemennt
    private lateinit var sensorManager: SensorManager

    //accelerometer sensor
    private var accelerometer: Sensor? = null

    //gyroscope sensor
    private var gyroscope: Sensor? = null



    //record last location values as -> store the previous tile values for stabilization


    //initalized to 0 initially
    private var lastRotationX = 0f
    private var lastRotationY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ar_activity)


        //check for camera permission
        checkAndRequestCameraPermission()

        // Initialize the LineChart
        lineChart = findViewById(R.id.lineChartAR)

        // Retrieve data from the progress fragment - grpah data and assignment names
        val dataEntries = intent.getSerializableExtra("dataEntries") as? ArrayList<Pair<Float, Float>>
        val homeworkNames = intent.getStringArrayExtra("homeworkNames")


       //check if data is available
        if (dataEntries != null && homeworkNames != null) {

            //start drawing the graph
            setupGraph(dataEntries, homeworkNames)



        } else {


            Toast.makeText(this, "No Graph Data. Cannot generate AR.", Toast.LENGTH_SHORT)
                .show()



        }

        // Initialize the camera -> background
       // initializeCamera()

        // Initialize sensors - tracking
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        // Add floating animation
        addFloatingAnimation()
    }



    //check for camera permission
    private fun checkAndRequestCameraPermission() {


        // Check if the camera permission is granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // permission to use camera not granted
            ActivityCompat.requestPermissions(


                this,
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {


            //granted skip and work on the camera
            initializeCamera()
        }
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, initialize the camera
                initializeCamera()
            } else {
                // Permission denied
                Toast.makeText(this, "Camera permission is required to use this feature.", Toast.LENGTH_SHORT).show()
            }
        }
    }



//initialize camera -> background
    private fun initializeCamera() {

        //camera provider -> manage lifecycle of the camera
        val cameraProviderFuture = androidx.camera.lifecycle.ProcessCameraProvider.getInstance(this)



    //once camera provider is ready
    cameraProviderFuture.addListener({

        //get the camera provider instance to interact with the camera
        val cameraProvider = cameraProviderFuture.get()


        //create a preview to show the camera feed in the backgroind
        val preview = androidx.camera.core.Preview.Builder().build().apply {


            //set the surface provider to the preview view
            setSurfaceProvider(findViewById<androidx.camera.view.PreviewView>(R.id.previewViewCamera).surfaceProvider)

            }


        //get the back camera
        val cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA

        //unbind all previous use of the camera -> prevents multiple instances of the camera -> could cause crash
        cameraProvider.unbindAll()

        //bind the camera to the lifecycle of the activity
        cameraProvider.bindToLifecycle(this, cameraSelector, preview)



        }, androidx.core.content.ContextCompat.getMainExecutor(this))
    }


    //draw the graph
    private fun setupGraph(dataEntries: ArrayList<Pair<Float, Float>>, homeworkNames: Array<String>) {


        // Convert data entries into Entry objects
        val entries = dataEntries.map { Entry(it.first, it.second) }

        // Create LineDataSet
        val dataSet = LineDataSet(entries, "Homework Progress")
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = ContextCompat.getColor(this, R.color.white)
        dataSet.setCircleColors(ContextCompat.getColor(this, R.color.accentColor))
        dataSet.setDrawCircles(true)
        dataSet.circleRadius = 5f
        dataSet.setDrawFilled(true)
        dataSet.fillDrawable = ContextCompat.getDrawable(this, R.drawable.gradient_background)

        // Set LineData to LineChart object


        val lineData = LineData(dataSet) //create data
        lineChart.data = lineData //set data to chart

        // X-Axis

        val xAxis = lineChart.xAxis


        xAxis.position = XAxis.XAxisPosition.BOTTOM


        xAxis.granularity = 1f





        //formating x values through a value formatter -> allows for custom x values
        //line chart by default uses index values for x axis
        //customizing to display names dynamically
        xAxis.valueFormatter = object : ValueFormatter() {


            //overriding the fet formating value to get text names from homework
            override fun getFormattedValue(value: Float): String {

                //get the index of the value
                val index = value.toInt()


                //return the corresponding homework name based on the index
                return if (index >= 0 && index < homeworkNames.size) homeworkNames[index] else ""
            }
        }

        // Customize Y-Axis
        val yAxisLeft = lineChart.axisLeft
        yAxisLeft.axisMinimum = 0f
        yAxisLeft.axisMaximum = 100f

        // no show right y axis
        val yAxisRight = lineChart.axisRight
        yAxisRight.isEnabled = false

        //chart settings
        lineChart.description.isEnabled = false
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)

        //zoom out ar effect -> places in the beginning
        lineChart.scaleX = 0.8f
        lineChart.scaleY = 0.8f

        // Animate chart when drawing
        lineChart.animateX(1500)
        lineChart.animateY(1500)

        //create the cart
        lineChart.invalidate()
    }


    //add a nicer animation for ar effect
    private fun addFloatingAnimation() {

        //create a value animator to animate the chart -> vertical movement of chart


        //movement between 0 and 10 pixels -> up and down
        val animator = ValueAnimator.ofFloat(0f, 10f).apply {

            //animation duration range
            duration = 2000


            repeatMode = ValueAnimator.REVERSE // makes chart float up  and down
            repeatCount = ValueAnimator.INFINITE // infinite animation


            //listener to for each frame
            //tracks animation progress
            //updates the line chart based on this
            addUpdateListener { animation ->

                //get the current value of the animation
                val value = animation.animatedValue as Float

                //plot based on animation
                lineChart.translationY = value
            }
        }

        //start the animation
        animator.start()
    }



    //lifecycle methods
    override fun onResume() {



        super.onResume()


        //  sensor listeners
        accelerometer?.let {


            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)


        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {


        super.onPause()


        // deregister sensor listeners
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {

            //track the movement of the phone
            when (it.sensor.type) {

                //accelorometer sensor
                Sensor.TYPE_ACCELEROMETER -> {

                    //get the tilt values
                    val tiltX = it.values[0] //X-axis

                    val tiltY = it.values[1] // Y-axis



                    //adjust the tilt values for better movemenet
                    val adjustedTiltX = (tiltX - lastRotationX) / 5f
                    val adjustedTiltY = (tiltY - lastRotationY) / 5f


                    //move the chart based on the tilt values
                    lineChart.translationX += adjustedTiltX * 10
                    lineChart.translationY += adjustedTiltY * 10


                    //store the last tilt values -> reference for next
                    //stabilize the movement of the chart because it is generating based on new
                    lastRotationX = tiltX
                    lastRotationY = tiltY
                }


                //gyroscope sensor
                Sensor.TYPE_GYROSCOPE -> {

                    //get the rotation values
                    val rotation = it.values[2] / 5f

                    //rotate y
                    lineChart.rotationY += rotation * 5
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        //do nothing
    }
}