package com.bupp.wood_spoon_eaters.managers


import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.annotation.NonNull
import com.bupp.wood_spoon_eaters.model.Address

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by MonkeyFather on 15/05/2018.
 */

class LocationManager(val context: Context, val permissionManager: PermissionManager) {

//    private lateinit var activity: Activity
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mSettingsClient: SettingsClient

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var mLocationCallback: LocationCallback? = null
    private var mCurrentLocation: Location? = null

    private var mRequestingLocationUpdates: Boolean = false
    private var mLastLocation: Location? = null

//    private var userCurrentAddress: Address? = null
    //    private TAction<Location> onLocationCatch;

    private var isStarted = false

    interface LocationManagerListener{
        fun onLocationChanged(mLocation: Address)
    }

    var listener: LocationManagerListener? = null
    fun setLocationManagerListener(listener: LocationManagerListener) {
        Log.d(TAG, "setLocationManagerListener")
        this.listener = listener
    }

    fun removeLocationManagerListener() {
        Log.d(TAG, "removeLocationListener")
        this.listener = null
    }

    val lastLatLng: LatLng?
        get() = if (mCurrentLocation != null) LatLng(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude) else LatLng(0.0, 0.0)

    fun start() {
        if (!isStarted) {
            Log.d(TAG, "startLocationUpdates")
            isStarted = true
//            this.activity = activity
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            mSettingsClient = LocationServices.getSettingsClient(context)

            if (mLocationCallback == null) {
                createLocationCallback()
            }
            if (!this::mLocationRequest.isInitialized) {
                createLocationRequest()
            }
            if (mLocationSettingsRequest == null) {
                buildLocationSettingsRequest()
            }

            //start Location Service
            if (!mRequestingLocationUpdates) {
                mRequestingLocationUpdates = true
                startLocationUpdates()
            }
        } else {
            Log.d(TAG, "re starting LocationUpdates")
            startLocationUpdates()
        }
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest.create().apply {
            interval = UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun createLocationCallback() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                mLastLocation = mCurrentLocation
                mCurrentLocation = locationResult.lastLocation
                if(listener != null){
                    Log.d(TAG, "onLocationResult:" + locationResult.lastLocation)
                    listener?.onLocationChanged(getAddressFromLocation(mCurrentLocation!!))
                }
            }
        }
    }



    private fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        mLocationSettingsRequest = builder.build()
        mSettingsClient = LocationServices.getSettingsClient(context)


    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        val task: Task<LocationSettingsResponse> = mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
        task.addOnSuccessListener {
            // All location settings are satisfied. The client can initialize
            // location requests here.
            mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())

        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                Log.d(TAG, "location setting failed")
                val statusCode = (exception as ApiException).getStatusCode()
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " + "location settings")
                        val rae = exception as ResolvableApiException
//                        rae.startResolutionForResult(context, Constants.REQUEST_CHECK_SETTINGS);
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage = "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings."
                        Log.e(TAG, errorMessage)
                        mRequestingLocationUpdates = false
                    }
                    else -> Log.d(TAG, "location setting failed big")
                }

            }
        }
    }

    fun stopLocationUpdates() {
        if (isStarted) {
            isStarted = false
            if ((!mRequestingLocationUpdates)) {
                Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.")
                return
            }
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback).addOnCompleteListener(object : OnCompleteListener<Void> {
                override fun onComplete(@NonNull task: Task<Void>) {
                    Log.d(TAG, "stopLocationUpdates")
                    mRequestingLocationUpdates = false
                }
            })

        }
    }


    fun getAddressFromLocation(location: Location): Address{
        val geocoder: Geocoder
        var addresses: List<android.location.Address> = arrayListOf()
        geocoder = Geocoder(context, Locale.getDefault())

        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        }catch (e: IOException){
            Log.d(TAG, "location manager error: " + e.message)
        }
        Log.d(TAG, "my location object: ${addresses[0]}")
        val streetLine = addresses[0].getAddressLine(0)
//        val city = addresses[0].locality
//        val state = addresses[0].adminArea
//        val country = addresses[0].countryName
//        val postalCode = addresses[0].postalCode
//        val knownName = addresses[0].featureName
        Log.d(TAG, "latlng to address success")

        var address = Address()
        address.streetLine1 = streetLine
        address.lat = mCurrentLocation!!.latitude
        address.lng = mCurrentLocation!!.longitude
        return address
    }

    fun getCurrentAddress(): Address? {
        if(mCurrentLocation != null){
            return getAddressFromLocation(mCurrentLocation!!)
        }
        return null
    }

    

    companion object {

        private val TAG = "wowLocationManager"
        private val TIMEOUT_IN_MILLISECONDS = (5 * 1000).toLong()

        private val UPDATE_INTERVAL_IN_MILLISECONDS = 10000.toLong()
        private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000.toLong()




    }


}