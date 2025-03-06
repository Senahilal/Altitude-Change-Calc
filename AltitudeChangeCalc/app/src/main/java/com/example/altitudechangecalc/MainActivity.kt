package com.example.altitudechangecalc

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.altitudechangecalc.ui.theme.AltitudeChangeCalcTheme
import kotlin.math.pow

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var pressureSensor: Sensor? = null

    private var pressureReading by mutableStateOf(1013.25f) // Default pressure at sea level
    private var altitude by mutableStateOf(0f)
    private var _accuracy by mutableStateOf("Unknown")

    private var simulatedAltitude by mutableStateOf(0f) // Simulated altitude for UI
    //private var simulatedPressure by mutableStateOf(1013.25f) // Simulated altitude for UI


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Sensor Manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)


        setContent {
            AltitudeChangeCalcTheme {
                AltitudeScreen(pressureReading, altitude)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        pressureSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_PRESSURE) {
                pressureReading = it.values[0]

                //calling the fun created below
                altitude = calculateAltitude(pressureReading)
            }
        }
    }

    //using the given formula calculating the altitude
    private fun calculateAltitude(pressure: Float): Float {
        val P0 = 1013.25f // std pressure in hPa
        return 44330 * (1 - (pressure / P0).pow(1 / 5.255f))
    }

//    from class example
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        _accuracy = when (accuracy) {
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> "High"
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "Medium"
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "Low"
            SensorManager.SENSOR_STATUS_UNRELIABLE -> "Unreliable"
            else -> "Unknown"
        }
    }


}



@Composable
fun AltitudeScreen(pressure: Float, altitude: Float) {
    var simulatedPressureValue by remember { mutableStateOf(1013.25f) }

    //when simulatedPressureValue changes, calls calcAltitude
    val simulatedAltitudeValue = remember(simulatedPressureValue) {
        calcAltitude(simulatedPressureValue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundColor(simulatedAltitudeValue)) // Background based on simulated altitude
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Real Altitude Data",
            style = MaterialTheme.typography.headlineSmall,
            color = getTextColor(simulatedAltitudeValue))
        Text(
            text = "Pressure: ${pressure} hPa",
            style = MaterialTheme.typography.bodyLarge,
            color = getTextColor(simulatedAltitudeValue)
        )
        Text(
            text = "Altitude: ${altitude.toInt()} m",
            style = MaterialTheme.typography.bodyLarge,
            color = getTextColor(simulatedAltitudeValue),
        )


        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Simulated Data",
            style = MaterialTheme.typography.headlineSmall,
            color = getTextColor(simulatedAltitudeValue))
        Text(
            text = "Simulated Pressure: ${simulatedPressureValue} hPa",
            style = MaterialTheme.typography.bodyLarge,
            color = getTextColor(simulatedAltitudeValue)
        )
        Text(
            text = "Simulated Altitude: ${simulatedAltitudeValue.toInt()} m",
            style = MaterialTheme.typography.bodyLarge,
            color = getTextColor(simulatedAltitudeValue)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Buttons to simulate altitude changes
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Button(
                onClick = { simulatedPressureValue -= 100 }) {
                Text(text = "-100hPa")
            }
            Button(
                onClick = { simulatedPressureValue += 100 }) {
                Text(text = "+100hPa"
                )
            }
        }
    }
}


fun getBackgroundColor(altitude: Float): Color {
    return when {
        altitude < 0 -> Color(0xFFFFFFFF) //under sea level
        altitude < 100 -> Color(0xFFB0E0E6) // Pale blue
        altitude < 500 -> Color(0xFF87CEEB) // Light blue
        altitude < 1000 -> Color(0xFF4682B4) // Medium blue
        altitude < 2000 -> Color(0xFF1E3A5F) // Dark blue
        altitude < 3000 -> Color(0xFF0D253F) // Very dark blue
        else -> Color(0xFF000814) // Almost black
    }
}

fun getTextColor(altitude: Float): Color {
    return when {
        altitude < 1000 -> Color.Black
        else -> Color.White
    }
}

fun calcAltitude(pressure: Float): Float {
    val P0 = 1013.25f // std pressure in hPa
    return 44330 * (1 - (pressure / P0).pow(1 / 5.255f))
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AltitudeChangeCalcTheme {
    }
}