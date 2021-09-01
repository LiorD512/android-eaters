package com.bupp.wood_spoon_eaters.features.base

import android.app.Activity
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bupp.wood_spoon_eaters.BuildConfig
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.dialogs.super_user.SuperUserDialog
import com.bupp.wood_spoon_eaters.features.splash.SplashActivity
import kotlin.math.sqrt


open class BaseActivity : AppCompatActivity(), SuperUserDialog.SuperUserListener {

    private var superUserDialog: SuperUserDialog? = null
    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH
    }

    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta
//            Log.d(TAG, "shake acceleration: $acceleration")
            if (acceleration > SHAKING_RESISTANCE && (
                        superUserDialog == null ||
                        superUserDialog?.isVisible == false)
            ) {
                superUserDialog = SuperUserDialog()
                superUserDialog!!.show(supportFragmentManager, Constants.SUPER_USER_DIALOG)
                Toast.makeText(applicationContext, "Shake event detected", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun onResume() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "registered super user listener")
            sensorManager?.registerListener(sensorListener, sensorManager!!.getDefaultSensor(
                    Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        super.onResume()
    }

    override fun onPause() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "unregister super user listener")
            sensorManager!!.unregisterListener(sensorListener)
        }
        super.onPause()
    }

    override fun onEnvironmentChanged(forceRestart: Boolean?) {
        Log.d(TAG, "onEnvironmentChanged")
        if (forceRestart == true) {
            restartApp()
        }
    }

    open fun restartApp() {
        Log.d(TAG, "restartApp")
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        (this as Activity).finish()
        Runtime.getRuntime().exit(0)
    }

    companion object {
        const val TAG = "wowBaseActivity"
        const val SHAKING_RESISTANCE = 10

    }

}