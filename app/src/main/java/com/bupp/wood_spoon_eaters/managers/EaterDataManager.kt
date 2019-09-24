package com.bupp.wood_spoon_eaters.managers

import android.content.Context
import android.location.Location
import android.util.Log
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.utils.AppSettings
import com.bupp.wood_spoon_eaters.utils.Constants
import com.bupp.wood_spoon_eaters.utils.Utils
import com.stripe.android.model.PaymentMethod
import java.util.*


class EaterDataManager(val context: Context, val appSettings: AppSettings, val locationManager: LocationManager) :
    LocationManager.LocationManagerListener {

    private val TAG = "wowEaterAddressManager"
    private var lastChosenAddress: Address? = null
    private var currentPaymentMethod: PaymentMethod? = null

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

    override fun onLocationChanged(mLocation: Address) {
        val myAddress: Address? = getClosestAddressToLocation(mLocation)
        if(myAddress != null){
            setLastChosenAddress(mLocation)
            for(listener in listeners){
                listener?.onAddressChanged(myAddress)
            }
            listeners.clear()
            locationManager.removeLocationManagerListener()

        }else{
            for(listener in listeners){
                listener?.onAddressChanged(null)
            }
        }
    }


    private fun getClosestAddressToLocation(mLocation: Address): Address? {
        val myAddresses = currentEater?.addresses
        if(myAddresses != null && myAddresses.size > 0){
            for(address in myAddresses){
                if(isLocationsNear(mLocation.lat!!, mLocation.lng!!, address.lat!!, address.lng!!)){
                    return address
                }
            }
            return mLocation
        }else{
            return mLocation
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

    fun removeAddressById(deletedAddressId: Long) {
        if(currentEater != null){
            val addresess = currentEater!!.addresses
            val newAddresess: ArrayList<Address> = arrayListOf()
            for(item in addresess){
                if(item.id != deletedAddressId){
                   newAddresess.add(item)
                }
            }
            currentEater!!.addresses.clear()
            currentEater!!.addresses.addAll(newAddresess)
        }
    }

    fun updateAddressById(currentAddressId: Long, newAddress: Address?) {
        if(currentEater != null){
            val addresess = currentEater!!.addresses
            val newAddresess: ArrayList<Address> = arrayListOf()
            for(item in addresess){
                if(item.id != currentAddressId){
                    newAddresess.add(item)
                }
            }
            currentEater!!.addresses.clear()
            currentEater!!.addresses.add(newAddress!!)
            currentEater!!.addresses.addAll(newAddresess)
        }
    }




    //Stripe customer card
    fun updateCustomerCard(paymentMethod: PaymentMethod){
        this.currentPaymentMethod = paymentMethod
    }

    fun getCustomerCardId(): String{
        if(currentPaymentMethod != null){
            return currentPaymentMethod?.id!!
        }
        return ""
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

    fun getDropoffLocation(): String? {
        if(currentEater?.addresses != null && currentEater?.addresses!!.size > 0){
            return currentEater?.addresses?.first()?.getDropoffLocationStr()
        }else{
            return getLastChosenAddress()?.getDropoffLocationStr()
        }
    }




}