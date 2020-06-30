package com.bupp.wood_spoon_eaters.utils

import android.location.LocationManager.GPS_PROVIDER
import android.content.Context.LOCATION_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.location.LocationManager
import android.util.Log


class GPSBroadcastReceiver(val listener: GPSBroadcastListener) : BroadcastReceiver() {

    interface GPSBroadcastListener{
        fun onGPSChanged(isEnabled: Boolean)
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //isGPSEnabled = true;
                Log.d("wowGPSBroadcastReceiver", "GPS enabled")
                listener.onGPSChanged(true)
            } else {
                //isGPSEnabled = false;
                Log.d("wowGPSBroadcastReceiver", "GPS disabled")
                listener.onGPSChanged(false)
            }
        } catch (ex: Exception) {
        }
    }
}