package com.bupp.wood_spoon_eaters.managers

import android.content.Context
import android.location.Location
import android.util.Log
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.utils.AppSettings
import com.bupp.wood_spoon_eaters.utils.Constants
import com.bupp.wood_spoon_eaters.utils.Utils
import java.util.*


class EaterDataManager(val context: Context, val appSettings: AppSettings, val locationManager: LocationManager) :
    LocationManager.LocationManagerListener {

    private val TAG = "wowEaterAddressManager"
    private var lastChosenAddress: Address? = null

    interface EaterDataMangerListener{
        fun onAddressChanged(currentAddress: Address?)
    }

    //order location
    fun startLocationUpdates() {
        locationManager.start()
    }

    fun stopLocationUpdates() {
        locationManager.stopLocationUpdates()
    }


    //my location listener interface
    var listeners: ArrayList<EaterDataMangerListener> = arrayListOf()
    fun setLocationListener(listener: EaterDataMangerListener) {
        locationManager.setLocationManagerListener(this)
        listeners.add(listener)
    }

    fun removeLocationListener(listener: EaterDataMangerListener) {
        listeners.remove(listener)
        if(listeners.size == 0){
            locationManager.removeLocationManagerListener()
        }
    }

    override fun onLocationChanged(mLocation: Address) {
        val myAddress: Address? = getClosestAddressToLocation(mLocation)
        if(myAddress != null){
            setLastChosenAddress(mLocation)
            for(listener in listeners){
                listener?.onAddressChanged(myAddress)
            }
        }else{
            for(listener in listeners){
                listener?.onAddressChanged(null)
            }
        }
    }

    private fun getClosestAddressToLocation(mLocation: Address): Address? {
        val myAddresses = currentEater?.addresses
        if(myAddresses != null){
            for(address in myAddresses){
                if(isLocationsNear(mLocation.lat!!, mLocation.lng!!, address.lat!!, address.lng!!))
                    return address
            }
        }
        return null
    }

    fun isLocationsNear(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Boolean{
        val loc1 = Location("")
        loc1.setLatitude(lat1)
        loc1.setLongitude(lng1)

        val loc2 = Location("")
        loc2.setLatitude(lat2)
        loc2.setLongitude(lng2)

        return loc1.distanceTo(loc2) < Constants.MINIMUM_LOCATION_DISTANCE
    }



    fun getCurrentAddress(): Address? {
        return locationManager.getCurrentAddress()
    }

    fun getLastChosenAddress(): Address? {
        return lastChosenAddress ?: getCurrentAddress()
    }

    fun setLastChosenAddress(address: Address?) {
        this.lastChosenAddress = address
    }












    //order date and time !!
    var orderTime: Date? = null

    fun getLastOrderTime(): Date? {
        return if (orderTime != null) {
            orderTime
        } else {
            null
        }
    }

    fun getLastOrderTimeParam(): String? {
        //returns unix timestamp
        return if (getLastOrderTime() != null) {
            Utils.parseUnixTimestamp(getLastOrderTime()!!)
        } else {
            null
        }
    }

    fun getLastOrderTimeString(): String {
        return if (getLastOrderTime() != null) {
            Utils.parseDateToDayDateHour(getLastOrderTime()!!)
        } else {
            "ASAP"
        }
    }








    //eater params and data
    var currentEater: Eater? = null
    private var isUserChooseSpecificAddress: Boolean = false

    fun isAfterLogin(): Boolean {
        return !currentEater?.email.isNullOrEmpty()
    }

    fun setUserChooseSpecificAddress(isSpecificAddress: Boolean) {
        this.isUserChooseSpecificAddress = isSpecificAddress
    }
    fun isUserChooseSpecificAddress(): Boolean {
        //is user didn't chose my location
        return isUserChooseSpecificAddress
    }


}