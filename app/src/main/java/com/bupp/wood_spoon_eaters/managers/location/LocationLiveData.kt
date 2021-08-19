package com.bupp.wood_spoon_eaters.managers.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import com.bupp.wood_spoon_eaters.model.AddressRequest
import com.bupp.wood_spoon_eaters.utils.google_api_utils.GoogleAddressParserUtil
import com.google.android.gms.location.*
import java.io.IOException
import java.util.*

class LocationLiveData(val context: Context) : LiveData<AddressRequest>() {

    private var forceStop: Boolean = false
    private var isStarted =  false
    private var requestingLocationUpdates =  false
    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)


    override fun onInactive() {
        super.onInactive()
        Log.d(TAG,"onInactive")
        fusedLocationClient.removeLocationUpdates(locationCallback)
        isStarted = false
    }

    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()
        if (!isStarted) {
            isStarted = true
            Log.d(TAG,"onActive")
//            fusedLocationClient.lastLocation
//                .addOnSuccessListener { location: Location? ->
//                    location?.also {
//                        setLocationData(it)
//                    }
//                }.addOnFailureListener { error ->
//                    Log.d(TAG,"Failure: ${error.message}")
//
//                }
            startLocationUpdates()
        }

    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        Log.d(TAG,"startLocationUpdates")
        requestingLocationUpdates = true
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    private fun stopLocationUpdates() {
        if (isStarted) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            if(forceStop){
                Log.d(TAG,"forceStop: $forceStop")
                stopLocationUpdates()
            }else{
                Log.d(TAG,"onLocationResult")
                locationResult ?: return
                for (location in locationResult.locations) {
                    setLocationData(location)
                }
            }
        }

        override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
            Log.d(TAG, "LocationAvailability: ${locationAvailability?.isLocationAvailable}")
        }
    }

    private fun setLocationData(location: Location) {
        val accuracy = location.accuracy
        Log.d(TAG,"setLocationData - accuracy: $accuracy")
        val result = getAddressRequestFromLocation(location)
        result?.let{
            value = it
        }
    }

    private fun getAddressRequestFromLocation(location: Location): AddressRequest? {
        var addresses: List<android.location.Address> = arrayListOf()
        val geocoder: Geocoder = Geocoder(context, Locale.getDefault())

        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses.isNotEmpty()) {
                return GoogleAddressParserUtil.parseMyLocationToAddressRequest(addresses[0])
            }
        } catch (e: IOException) {
            Log.d(TAG, "location manager error: " + e.message)
        }
        return null
    }

    fun setForcedStop(forceStop: Boolean) {
        this.forceStop = forceStop
    }


    companion object {
        const val TAG = "wowLocationLiveData"
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 1
        }
    }
}
