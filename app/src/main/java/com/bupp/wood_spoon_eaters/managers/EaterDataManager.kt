package com.bupp.wood_spoon_eaters.managers

import android.content.Context
import android.location.Location
import android.util.Log
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.model.Event
import com.bupp.wood_spoon_eaters.utils.AppSettings
import com.bupp.wood_spoon_eaters.utils.Constants
import com.bupp.wood_spoon_eaters.utils.Utils
import com.stripe.android.model.PaymentMethod
import java.util.*
import android.provider.Settings.Secure
import android.provider.Settings.Secure.LOCATION_MODE_OFF
import android.provider.Settings.Secure.LOCATION_MODE
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.provider.Settings.SettingNotFoundException




class EaterDataManager(val context: Context, val appSettings: AppSettings, val locationManager: LocationManager) :
    LocationManager.LocationManagerListener {


    private val TAG = "wowEaterAddressManager"
    private var currentPaymentMethod: PaymentMethod? = null

    interface EaterDataMangerListener {
        fun onAddressChanged(currentAddress: Address?)
        fun onLocationEmpty() {}
        fun onUsingPreviousLocation() {}
    }

    //order location
    fun startLocationUpdates() {
        locationManager.start()
    }

    fun stopLocationUpdates() {
        locationManager.stopLocationUpdates()
    }


    //my location listener interface
    var listeners: MutableSet<EaterDataMangerListener> = mutableSetOf()

    fun setLocationListener(listener: EaterDataMangerListener) {
        locationManager.setLocationManagerListener(this)
        listeners.add(listener)
    }

    override fun onLocationChanged(mLocation: Address) {
        val myAddress: Address? = getClosestAddressToLocation(mLocation)
        if (myAddress != null) {
            setLastChosenAddress(mLocation)
            for (listener in listeners) {
                listener.onAddressChanged(myAddress)
            }
//            listeners.clear()
            locationManager.removeLocationManagerListener()

        } else {
            for (listener in listeners) {
                listener.onAddressChanged(null)
            }
        }
    }

    fun getListOfAddresses(): ArrayList<Address>? {
        currentEater?.let {
            return it.addresses
        }
        return null
    }

    override fun onLocationEmpty() {
        if (getListOfAddresses() == null || getListOfAddresses()!!.isEmpty()) {
            //if user never saved a location -> will show dialog
            listeners.forEach {
                it.onLocationEmpty()
            }
        } else {
            getListOfAddresses()?.let {
                onLocationChanged(it.last())

                listeners.forEach {
                    it.onUsingPreviousLocation()
                }
            }
        }

    }




    private fun getClosestAddressToLocation(mLocation: Address): Address? {
        val myAddresses = currentEater?.addresses
        if (myAddresses != null && myAddresses.size > 0) {
            for (address in myAddresses) {
                if (isLocationsNear(mLocation.lat!!, mLocation.lng!!, address.lat!!, address.lng!!)) {
                    return address
                }
            }
            return mLocation
        } else {
            return mLocation
        }
    }

    fun isLocationsNear(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Boolean {
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

    private var lastChosenAddress: Address? = null
    private var eventChosenAddress: Address? = null
    private var previousChosenAddress: Address? = null

    fun getLastChosenAddress(): Address? {
        when (isInEvent) {
            true -> return eventChosenAddress ?: null
            false -> return lastChosenAddress ?: getCurrentAddress()
        }
    }

    fun setLastChosenAddress(address: Address?) {
        when (isInEvent) {
            true -> this.eventChosenAddress = address
            false -> this.lastChosenAddress = address
        }
    }

    fun setPreviousChosenAddress(previousChosenAddress: Address?) {
        this.previousChosenAddress = previousChosenAddress
    }

    fun rollBackToPreviousAddress() {
        previousChosenAddress?.let {
            setLastChosenAddress(previousChosenAddress)
        }
    }

    fun removeAddressById(deletedAddressId: Long) {
        if (currentEater != null) {
            val addresess = currentEater!!.addresses
            val newAddresess: ArrayList<Address> = arrayListOf()
            for (item in addresess) {
                if (item.id != deletedAddressId) {
                    newAddresess.add(item)
                }
            }
            currentEater!!.addresses.clear()
            currentEater!!.addresses.addAll(newAddresess)
        }
    }

    fun updateAddressById(currentAddressId: Long, newAddress: Address?) {
        if (currentEater != null) {
            val addresess = currentEater!!.addresses
            val newAddresess: ArrayList<Address> = arrayListOf()
            for (item in addresess) {
                if (item.id != currentAddressId) {
                    newAddresess.add(item)
                }
            }
            currentEater!!.addresses.clear()
            currentEater!!.addresses.add(newAddress!!)
            currentEater!!.addresses.addAll(newAddresess)
        }
    }


    //Stripe customer card
    fun updateCustomerCard(paymentMethod: PaymentMethod) {
        this.currentPaymentMethod = paymentMethod
    }

    fun getCurrentPaymentMethod(): PaymentMethod?{
        return currentPaymentMethod
    }

    fun getCustomerCardId(): String {
        if (currentPaymentMethod != null) {
            return currentPaymentMethod?.id!!
        }
        return ""
    }


    var isInEvent: Boolean = false

    var hasSpecificTime: Boolean = false //this param indicates whether a user specified a time for search

    //order date and time !!
    var orderTime: Date? = null
    var eventOrderTime: Date? = null

    fun getFeedSearchTime(): Date? {
        if (hasSpecificTime) {
            return getLastOrderTime()
        } else {
            return null
        }
    }

    fun getFeedSearchTimeString(): String? {
        if (hasSpecificTime) {
            return getLastOrderTimeString()
        } else {
            return null
        }
    }

    fun getFeedSearchTimeStringParam(): String? {
        if (hasSpecificTime) {
            return getLastOrderTimeParam()
        } else {
            return null
        }
    }


    fun getLastOrderTime(): Date? {
        return when (isInEvent) {
            true -> {
                if (eventOrderTime != null) {
                    eventOrderTime
                } else {
                    null
                }
            }
            false -> {
                if (orderTime != null) {
                    orderTime
                } else {
                    null
                }
            }
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
        return when (isInEvent) {
            true -> false
            false -> isUserChooseSpecificAddress
        }

    }

    fun getDropoffLocation(): String? {
        if (currentEater?.addresses != null && currentEater?.addresses!!.size > 0) {
            return currentEater?.addresses?.first()?.getDropoffLocationStr()
        } else {
            return getLastChosenAddress()?.getDropoffLocationStr()
        }
    }

    fun setEventTimeAndPlace(event: Event?) {
        event?.let {
            isInEvent = true
            setLastChosenAddress(event.location)
            eventOrderTime = event.startsAt
        }
    }

    fun disableEventDate() {
        Log.d(TAG, "disableEventDate")
        isInEvent = false
        eventChosenAddress = null
        eventOrderTime = null
    }

    fun setUserChooseSpecificTime(hasSpecificTime: Boolean) {
        this.hasSpecificTime = hasSpecificTime
    }

    var sid: String? = null
    var cid: String? = null
    fun setUserCampaignParam(sid: String? = null, cid: String? = null) {
        sid?.let{
            this.sid = it
        }
        cid?.let{
            this.cid = it
        }
    }


}