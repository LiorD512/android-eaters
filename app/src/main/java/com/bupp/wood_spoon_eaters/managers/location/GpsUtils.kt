package com.bupp.wood_spoon_eaters.managers.location

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.model.Address
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.SettingsClient

class GpsUtils(private val context: Context? = null) {

    companion object{
        const val TAG = "wowGpsUtil"
    }

    private var isGPSEnabled: Boolean? = false
    private val locationSettingsRequest: LocationSettingsRequest?
    private lateinit var settingsClient: SettingsClient
    private lateinit var locationManager: LocationManager

    init {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(LocationLiveData.locationRequest)
        locationSettingsRequest = builder.build()
        builder.setAlwaysShow(true)

        context?.let{
            settingsClient = LocationServices.getSettingsClient(it)
            locationManager = it.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }

    fun turnGPSOn(OnGpsListener: OnGpsListener?) {
        context?.let{
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                OnGpsListener?.gpsStatus(true)
                isGPSEnabled = true
            } else {
                settingsClient
                    .checkLocationSettings(locationSettingsRequest)
                    .addOnSuccessListener(it as Activity) {
                        //  GPS is already enable, callback GPS status through listener
                        OnGpsListener?.gpsStatus(true)
                        isGPSEnabled = true
                    }
                    .addOnFailureListener(it) { e ->
                        when ((e as ApiException).statusCode) {
                            LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->

                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    val rae = e as ResolvableApiException
                                    rae.startResolutionForResult(it, 101)
                                } catch (sie: IntentSender.SendIntentException) {
                                    Log.i(TAG, "PendingIntent unable to execute request.")
                                }

                            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                                val errorMessage =
                                    "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings."
                                Log.e(TAG, errorMessage)

                                Toast.makeText(it, errorMessage, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
            }
        }

    }

    interface OnGpsListener {
        fun gpsStatus(isGPSEnable: Boolean)
    }

    fun getClosestAddressToLocation(mLocation: Address?, userAddresses: List<Address>): Address? {
        if (mLocation != null && userAddresses.isNotEmpty()) {
            for (address in userAddresses) {
                if (isLocationsNear(mLocation.lat!!, mLocation.lng!!, address.lat!!, address.lng!!)) {
                    return address
                }
            }
        }
        return null
    }

    private fun isLocationsNear(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Boolean {
        val loc1 = Location("")
        loc1.latitude = lat1
        loc1.longitude = lng1

        val loc2 = Location("")
        loc2.latitude = lat2
        loc2.longitude = lng2

        return loc1.distanceTo(loc2) < Constants.MINIMUM_LOCATION_DISTANCE
    }
}