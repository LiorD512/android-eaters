package com.bupp.wood_spoon_chef.presentation.features.base

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bupp.wood_spoon_chef.BuildConfig
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.common.WSProgressBar
import com.bupp.wood_spoon_chef.di.abs.LiveEvent
import com.bupp.wood_spoon_chef.presentation.dialogs.super_user.SuperUserDialog
import com.bupp.wood_spoon_chef.presentation.features.splash.SplashActivity
import com.bupp.wood_spoon_chef.data.remote.network.base.MTError
import com.bupp.wood_spoon_chef.utils.extensions.clearStack
import com.bupp.wood_spoon_chef.utils.extensions.getErrorMsgByType
import com.bupp.wood_spoon_chef.utils.extensions.showErrorToast
import io.shipbook.shipbooksdk.ShipBook
import kotlin.math.sqrt


open class BaseActivity : AppCompatActivity(), SuperUserDialog.SuperUserListener {

    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        ShipBook.screen(this.javaClass.simpleName)
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH
    }

    open fun handleErrorEvent(errorEvent: LiveEvent<MTError>, viewGroup: ViewGroup?) {
        errorEvent.getContentIfNotHandled()?.let { mtError ->
            viewGroup?.let {
                showErrorToast(mtError.getErrorMsgByType(), viewGroup, Toast.LENGTH_LONG)
            }
        }
    }

    open fun handleErrorEvent(error: MTError, viewGroup: ViewGroup?) {
        viewGroup?.let {
            showErrorToast(error.getErrorMsgByType(), viewGroup, Toast.LENGTH_LONG)
        }
    }

    open fun handleProgressBar(isLoading: Boolean) {
        if (isLoading) {
            WSProgressBar.instance()?.show(this)
        } else {
            WSProgressBar.instance()?.dismiss()
        }
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

            if (acceleration > SHAKING_RESISTANCE) {
                SuperUserDialog().show(supportFragmentManager, Constants.SUPER_USER_DIALOG)
                Toast.makeText(applicationContext, "Shake event detected", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun onResume() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "registered super user listener")
            sensorManager?.registerListener(
                sensorListener, sensorManager!!.getDefaultSensor(
                    Sensor.TYPE_ACCELEROMETER
                ), SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        super.onResume()
    }

    override fun onPause() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "unregister super user listener")
            sensorManager?.unregisterListener(sensorListener)
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
        intent.clearStack()
        startActivity(intent)
        finish()

        Runtime.getRuntime().exit(0)
    }


    companion object {
        const val TAG = "wowBaseActivity"
        const val SHAKING_RESISTANCE = 10

    }


}