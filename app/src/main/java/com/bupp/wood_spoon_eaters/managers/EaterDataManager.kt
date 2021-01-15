package com.bupp.wood_spoon_eaters.managers

import android.app.Activity
import android.content.Context
import android.location.Location
import android.util.Log
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.model.Event
import com.bupp.wood_spoon_eaters.common.AppSettings
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.di.abs.LiveEvent
import com.bupp.wood_spoon_eaters.features.main.feed.FeedViewModel
import com.bupp.wood_spoon_eaters.managers.location.GpsUtils
import com.bupp.wood_spoon_eaters.managers.location.LocationLiveData
import com.bupp.wood_spoon_eaters.model.FeedRequest
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.bupp.wood_spoon_eaters.utils.Utils
import com.stripe.android.model.PaymentMethod
import java.util.*


class EaterDataManager(val context: Context, val appSettings: AppSettings, val locationManager: LocationManager, val userRepository: UserRepository) :
    LocationManager.LocationManagerListener {

    private val TAG = "wowEaterAddressManager"

    val currentEater: Eater?
    get() = userRepository.getUser()

    /////////////////////////////////////////
    ///////////    LOCATION       ///////////
    /////////////////////////////////////////

    fun initGpsStatus(activity: Activity) {
        locationManager.checkGpsStatus(activity)
    }

    data class LocationStatus(val type: LocationStatusType, val address: Address? = null)
    enum class LocationStatusType{
        CURRENT_LOCATION,
        CURRENT_LOCATION_WITH_BANNER,
        KNOWN_LOCATION,
        KNOWN_LOCATION_WITH_BANNER,
        NO_GPS_ENABLED_AND_NO_LOCATION,
        HAS_GPS_ENABLED_BUT_NO_LOCATION,
        NO_GPS_PERMISSION,

    }

    fun getLocationStatus(): LocationStatus {
        val knownAddresses = getListOfAddresses()
        val myLocation = locationManager.getLocationData().value
        val hasGpsPermission = appSettings.hasGPSPermission
        val isGpsEnabled = locationManager.isGpsEnabled
        Log.d("wowEatersDataManager","getLocationStatus: hasGpsPermission: $hasGpsPermission, isGpsEnabled: $isGpsEnabled, knownAddresses: $knownAddresses, myLocation: $myLocation ")
        return if(hasGpsPermission && isGpsEnabled){
            Log.d("wowEatersDataManager", "has gpsPermission")
            if(knownAddresses.isNullOrEmpty()){
                Log.d("wowEatersDataManager", "don't have known address")
                if(myLocation != null){
                    LocationStatus(LocationStatusType.CURRENT_LOCATION, myLocation)
                }else{
                    LocationStatus(LocationStatusType.HAS_GPS_ENABLED_BUT_NO_LOCATION)
                }
            }else{
                Log.d("wowEatersDataManager", "has known address")
                if(myLocation != null){
                    val closestAddress = GpsUtils().getClosestAddressToLocation(myLocation, knownAddresses)
                    if(closestAddress != null){
                        Log.d("wowEatersDataManager", "using closest address: $closestAddress")
                        LocationStatus(LocationStatusType.KNOWN_LOCATION, closestAddress)
                    }else{
                        Log.d("wowEatersDataManager", "using current location: $myLocation")
                        LocationStatus(LocationStatusType.CURRENT_LOCATION, myLocation)
                    }
                }else{
                    LocationStatus(LocationStatusType.KNOWN_LOCATION_WITH_BANNER, knownAddresses[0])
                }
            }
        }else{ // GPS permission denied
            Log.d("wowEatersDataManager", "don't have gpsPermission")
            if(knownAddresses.isNullOrEmpty()){
                LocationStatus(LocationStatusType.NO_GPS_ENABLED_AND_NO_LOCATION)
            }else{
                LocationStatus(LocationStatusType.KNOWN_LOCATION_WITH_BANNER, knownAddresses[0])
            }
        }
    }

    fun getLocationData(): LocationLiveData {
        return locationManager.getLocationData()
    }


    fun getFinalTimeAndLocationParam(): FeedRequest {
        var feedRequest = FeedRequest()
        //address
        val currentAddress = getLastChosenAddress()
        if (isUserChooseSpecificAddress()) {
            feedRequest.addressId = currentAddress?.id
        } else {
            feedRequest.lat = currentAddress?.lat
            feedRequest.lng = currentAddress?.lng
        }

        //time
        feedRequest.timestamp = getLastOrderTimeParam()

        return feedRequest
    }








//    fun getLocationLiveEvent(): LocationLiveData {
//        return locationManager.locationData
//    }
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

    //location manager
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

    private fun getListOfAddresses(): List<Address>? {
        currentEater?.let {
            return it.addresses
        }
        return null
    }

    //location manager
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



    //address utils
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
    //address utils
    private fun isLocationsNear(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Boolean {
        val loc1 = Location("")
        loc1.latitude = lat1
        loc1.longitude = lng1

        val loc2 = Location("")
        loc2.latitude = lat2
        loc2.longitude = lng2

        return loc1.distanceTo(loc2) < Constants.MINIMUM_LOCATION_DISTANCE
    }


    fun getCurrentAddress(): Address? {
        return locationManager.getCurrentAddress()
    }

    private var lastChosenAddress: Address? = null
    private var eventChosenAddress: Address? = null
    private var previousChosenAddress: Address? = null

    fun getLastChosenAddress(): Address? {
        return when (isInEvent) {
            true -> eventChosenAddress
            false -> lastChosenAddress ?: getLocationStatus().address
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

    // user repo
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

    // user repo
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

    //feed repo
    fun getFeedSearchTime(): Date? {
        if (hasSpecificTime) {
            return getLastOrderTime()
        } else {
            return null
        }
    }
    //feed repo
    fun getFeedSearchTimeString(): String? {
        if (hasSpecificTime) {
            return getLastOrderTimeString()
        } else {
            return null
        }
    }
    //feed repo
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
            DateUtils.parseUnixTimestamp(getLastOrderTime()!!)
        } else {
            null
        }
    }

    fun getLastOrderTimeString(): String {
        return if (getLastOrderTime() != null) {
            DateUtils.parseDateToDayDateHour(getLastOrderTime()!!)
        } else {
            "ASAP"
        }
    }


    //eater params and data

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