package com.example.studyflow.additionalClasses



//Shake detector -> used for shake to undo for both courses and homework

//imports for sensors
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.SensorEventListener
import android.hardware.SensorEvent
import android.util.Log

class ShakeDetector(private val onShake: () -> Unit) : SensorEventListener {



    //variables
    private var lastUpdate: Long = 0
    private var shakeTreshold = 10f //sensetivity
    private var shakeTimeLimit = 10000 //min time between sahkes


    //overriding onsensors changed
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]


            //get the acceleration
            val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble()).toDouble()


            //check if the acceleration is greater than the treshold
            if (acceleration > shakeTreshold) {

                //get the current time
                val currentTime = System.currentTimeMillis()

                //check if the time between the shakes is greater than the limit
                if (currentTime - lastUpdate > shakeTimeLimit) {

                    lastUpdate = currentTime //update the time

                    Log.d("ShakeDetector", "Shake detected on Pixel 7!")


                    onShake()


                }
            }



            }
        }

    //override onaccuracy changed
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

//do nothing for now


    }

}



//end of the class









