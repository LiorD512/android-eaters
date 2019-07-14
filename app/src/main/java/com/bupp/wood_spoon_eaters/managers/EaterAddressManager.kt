package com.bupp.wood_spoon_eaters.managers

import android.content.Context
import android.util.Log
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.utils.AppSettings


class EaterAddressManager(val context: Context, val appSettings: AppSettings, val locationManager: LocationManager) {


    private val TAG = "wowEaterAddressManager"
    private var lastChosenAddress: Address? = null




//    private fun hasAddress(): Boolean {
//        return appSettings.currentEater?.addresses?.size!! > 0
//    }
//
//    private fun getLastOrderAddress(): Address? {
//        return if (hasAddress()) {
//            appSettings.currentEater?.addresses!!.last()
//        } else {
//            null
//        }
//    }
//
//    fun shouldUseLatLng(): Boolean {
//        val currentLatLng = locationManager.getCurrentAddress()
//        val curAddress = getLastOrderAddress()
//        Log.d(TAG, "currentLatLng: $currentLatLng, curAddress: $curAddress")
//        return curAddress == null
//    }
//
//    fun getLastLocation(): Address? {
//        if (shouldUseLatLng()) {
//            return locationManager.getAddressFromLatLng()
//        } else {
//            return getLastOrderAddress()
//        }
//    }

    fun getCurrentAddress(): Address? {
        return locationManager.getCurrentAddress()
    }

    fun startLocationUpdates() {
        locationManager.start()
    }

    fun stopLocationUpdates() {
        locationManager.stopLocationUpdates()
    }

    fun setLocationListener(listener: LocationManager.LocationManagerListener) {
        locationManager.setLocationManagerListener(listener)
    }

    fun removeLocationListener(listener: LocationManager.LocationManagerListener) {
        locationManager.removeLocationManagerListener(listener)
    }


    fun getLastChosenAddress(): Address? {
        return lastChosenAddress ?: getCurrentAddress()
    }

    fun setLastChosenAddress(address: Address?) {
        this.lastChosenAddress = address
    }


}