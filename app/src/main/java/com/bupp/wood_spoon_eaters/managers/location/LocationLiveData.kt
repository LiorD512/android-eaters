package com.bupp.wood_spoon_eaters.managers.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.AddressRequest
import com.bupp.wood_spoon_eaters.utils.GoogleAddressParserUtil
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.*

class LocationLiveData(val context: Context) : LiveData<AddressRequest>() {

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
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.also {
                        setLocationData(it)
                    }
                }
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

//    private fun stopLocationUpdates() {
//        if (isStarted) {
//            fusedLocationClient.removeLocationUpdates(locationCallback)
//        }
//    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            Log.d(TAG,"onLocationResult")
            locationResult ?: return
            for (location in locationResult.locations) {
                setLocationData(location, true)
            }
        }
    }

    private fun setLocationData(location: Location, isFinalResult: Boolean = false) {
        Log.d(TAG,"setLocationData:")
        val accuracy = location.accuracy
        Log.d(TAG,"onLocationResult accuracy: $accuracy")
        value = getAddressRequestFromLocation(location)
    }

    fun getAddressRequestFromLocation(location: Location): AddressRequest? {
        var addresses: List<android.location.Address> = arrayListOf()
        val geocoder: Geocoder = Geocoder(context, Locale.getDefault())

        var streetLine = ""
        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses.isNotEmpty()) {
                return GoogleAddressParserUtil.parseMyLocationToAddressRequest(addresses[0])
            }
        } catch (e: IOException) {
            Log.d(TAG, "location manager error: " + e.message)
//            Toast.makeText(context, "location manager error: " + e.message, Toast.LENGTH_SHORT).show()
        }
        return null
    }

//    fun getAddressFromLocation(location: Location): Address {
//        var addresses: List<android.location.Address> = arrayListOf()
//        val geocoder: Geocoder = Geocoder(context, Locale.getDefault())
//
//        var streetLine = ""
//        try {
//            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
//            if (addresses.isNotEmpty()) {
//                streetLine = getStreetStr(addresses[0])
//                Log.d(TAG,"streetLine: $streetLine")
//            }
//        } catch (e: IOException) {
//            Log.d(TAG, "location manager error: " + e.message)
////            Toast.makeText(context, "location manager error: " + e.message, Toast.LENGTH_SHORT).show()
//        }
//
//        Log.d(TAG, "latlng to address success")
//
//        var address = Address()
//        address.streetLine1 = streetLine
//        address.lat = location.latitude
//        address.lng = location.longitude
//        return address
//    }

    private fun getStreetStr(address: android.location.Address): String {
        var number = ""
        var street = ""
        var city = ""
        if (address.featureName != null) {
            number = address.featureName
        }
        if (address.thoroughfare != null) {
            street = address.thoroughfare
        }
        if (address.locality != null) {
            city = address.locality
        }
        return "$number $street"//, $city"
    }

    companion object {
        const val TAG = "wowLocationLiveData"
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
}
