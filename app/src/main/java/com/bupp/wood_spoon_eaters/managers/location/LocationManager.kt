package com.bupp.wood_spoon_eaters.managers.location


import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.location.Location
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.annotation.NonNull
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.repositories.UserRepository

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task


/**
 * Created by MonkeyFather on 15/05/2018.
 */

class LocationManager(val context: Context, private val userRepository: UserRepository){//} : GPSBroadcastReceiver.GPSBroadcastListener {


    /////////////////////////////////////////
    /////////////    LOCATION    ////////////
    /////////////////////////////////////////

    fun getLocationData() = locationLiveData
    private val locationLiveData = LocationLiveData(context)

    fun getFinalAddressLiveData() = finalAddressLiveData
    private val finalAddressLiveData = MutableLiveData<Address?>()
//    private val getFinalAddress = updateFinalAddress()

    fun updateFinalAddress() {//todo - check this - ny -
        // return nearest known address to users current location
        // if not, return user current location
        // if not, return user last known location saved by user
        // if not, return null.
        Log.d(TAG, "updateFinalAddress")
        var finalAddress: Address? = null
        val knownAddresses = getListOfAddresses()
        val myLocation = getLocationData().value
        if (knownAddresses.isNullOrEmpty()) {
            Log.d("LocationManager", "don't have known address")
            myLocation?.let {
                Log.d(TAG, "updateFinalAddress = user current location")
                finalAddress = it
            }
        } else {
            val closestAddress = GpsUtils().getClosestAddressToLocation(myLocation, knownAddresses)
            closestAddress?.let {
                Log.d(TAG, "updateFinalAddress - closest location")
                finalAddress = it
            }
            finalAddress = knownAddresses[0]
            Log.d(TAG, "updateFinalAddress - known location")
        }
        Log.d(TAG, "updateFinalAddress - postValue")
        finalAddressLiveData.postValue(finalAddress)
    }

    private fun getListOfAddresses(): List<Address>? {
        userRepository.getUser()?.let {
            return it.addresses
        }
        return null
    }

    /////////////////////////////////////////
    ////////////       GPS       ////////////
    /////////////////////////////////////////

//    fun getGpsData() = gpsLiveData
//    private val gpsLiveData = LiveEventData<Boolean>()
//
//    private lateinit var gpsBroadcastReceiver: GPSBroadcastReceiver
//    var isGpsEnabled: Boolean = true
//
//    init {
//        registerGpsBroadcastReceiver()
//    }
//
//    private fun registerGpsBroadcastReceiver() {
//        gpsBroadcastReceiver = GPSBroadcastReceiver(this)
//        context.registerReceiver(gpsBroadcastReceiver, IntentFilter("android.location.PROVIDERS_CHANGED"))
//    }
//
//    override fun onGPSChanged(isEnabled: Boolean) {
//        Log.d("wowLocationManager", "onGPSChanged: $isEnabled")
//        isGpsEnabled = isEnabled
//        gpsLiveData.postRawValue(isEnabled)
//    }

//    fun checkGpsStatus(context: Context) {
//        GpsUtils(context).turnGPSOn(object : GpsUtils.OnGpsListener {
//            override fun gpsStatus(isGPSEnable: Boolean) {
//                Log.d("wowLocationManager", "gpsStatus: $isGPSEnable")
//                isGpsEnabled = isGPSEnable
//            }
//        })
//    }


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

    interface LocationManagerListener {
        fun onLocationChanged(mLocation: Address)
        fun onLocationEmpty()
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

//    val lastLatLng: LatLng?
//        get() = if (mCurrentLocation != null) LatLng(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude) else LatLng(0.0, 0.0)

    fun start() {
        if (!isStarted) {
//            Toast.makeText(context, "starting location manager", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "startLocationManager")
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
//            Toast.makeText(context, "Re-starting location manager", Toast.LENGTH_SHORT).show()
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
//        Log.d(TAG, "createLocationCallback")
//        mLocationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                mLastLocation = mCurrentLocation
//                mCurrentLocation = locationResult.lastLocation
//                if (listener != null) {
////                    Toast.makeText(context, "onLocationResult:" + locationResult.lastLocation, Toast.LENGTH_SHORT).show()
//                    Log.d(TAG, "onLocationResult:" + locationResult.lastLocation)
//                    listener?.onLocationChanged(getAddressFromLocation(mCurrentLocation!!))
//                }
//            }
//        }
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
            Log.d(TAG, "location update success")
            // All location settings are satisfied. The client can initialize
            // location requests here.
            mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                Log.d(TAG, "location setting failed")
//                Toast.makeText(context, "location setting failed", Toast.LENGTH_SHORT).show()
                val statusCode = (exception as ApiException).statusCode
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
//                        Toast.makeText(context, "location setting failed - RESOLUTION_REQUIRED", Toast.LENGTH_SHORT).show()
                        Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " + "location settings")
                        if (!isLocationEnabled(context)) {
                            listener?.onLocationEmpty()
                        }
                        val rae = exception as ResolvableApiException
//                        rae.startResolutionForResult(context, Constants.REQUEST_CHECK_SETTINGS);
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
//                        Toast.makeText(context, "location setting failed - SETTINGS_CHANGE_UNAVAILABLE", Toast.LENGTH_SHORT).show()
                        val errorMessage = "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings."
                        Log.e(TAG, errorMessage)
                        mRequestingLocationUpdates = false
                    }
                    else -> Log.d(TAG, "location setting failed big")
                }
            }
        }
    }

    private fun isLocationEnabled(context: Context): Boolean {
        var locationMode = 0
        val locationProviders: String

        try {
            locationMode = Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)

        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
            return false
        }

        return locationMode != Settings.Secure.LOCATION_MODE_OFF


    }

    fun stopLocationUpdates() {
        if (isStarted) {
            isStarted = false
            if ((!mRequestingLocationUpdates)) {
//                Toast.makeText(context, "stopLocationUpdates: updates never requested", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.")
                return
            }
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback).addOnCompleteListener(object : OnCompleteListener<Void> {
                override fun onComplete(@NonNull task: Task<Void>) {
//                    Toast.makeText(context, "stopLocationUpdates", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "stopLocationUpdates")
                    mRequestingLocationUpdates = false
                }
            })

        }
    }


    fun getCurrentAddress(): Address? {
//        if (mCurrentLocation != null) {
//            return getAddressFromLocation(mCurrentLocation!!)
//        }
        return null
    }


    companion object {

        private val TAG = "wowLocationManager"
        private val TIMEOUT_IN_MILLISECONDS = (5 * 1000).toLong()

        private val UPDATE_INTERVAL_IN_MILLISECONDS = 10000.toLong()
        private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000.toLong()


    }


}