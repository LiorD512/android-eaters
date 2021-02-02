package com.bupp.wood_spoon_eaters.managers

import android.app.Activity
import android.content.Context
import android.location.Location
import android.util.Log
import com.bupp.wood_spoon_eaters.common.AppSettings
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import com.bupp.wood_spoon_eaters.managers.location.GpsUtils
import com.bupp.wood_spoon_eaters.managers.location.LocationManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.stripe.android.model.PaymentMethod
import java.util.*


class EaterDataManager(val context: Context, private val appSettings: AppSettings, private val locationManager: LocationManager,
                       val deliveryTimeManager: DeliveryTimeManager, private val userRepository: UserRepository) :
    LocationManager.LocationManagerListener {

    val currentEater: Eater?
    get() = userRepository.getUser()

    /////////////////////////////////////////
    ////////    DELIVERY_TIME       /////////
    /////////////////////////////////////////

    fun getDeliveryTimeLiveData() = deliveryTimeManager.getDeliveryTimeLiveData()

    /////////////////////////////////////////
    ///////////    LOCATION       ///////////
    /////////////////////////////////////////

    fun getFinalAddressLiveData() = locationManager.getFinalAddressLiveData() // used for feed !
    fun getLocationData() = locationManager.getLocationData()
//    fun getGpsData() = locationManager.getGpsData()
    fun initGpsStatus(activity: Activity) {
//        locationManager.checkGpsStatus(activity)
    }

    fun getLocationStatus(): LocationStatus {
        val finalAddress = locationManager.getFinalAddressLiveData().value //todo - ny
        val knownAddresses = getListOfAddresses()
        val myLocation = locationManager.getFinalAddressLiveData().value
        val hasGpsPermission = appSettings.hasGPSPermission
        val isGpsEnabled = true//locationManager.isGpsEnabled
        Log.d(
            "wowEatersDataManager",
            "getLocationStatus: finalAddress: $finalAddress, hasGpsPermission: $hasGpsPermission, isGpsEnabled: $isGpsEnabled, knownAddresses: $knownAddresses, myLocation: $myLocation "
        )
        return if (hasGpsPermission && isGpsEnabled) {
            Log.d("wowEatersDataManager", "has gpsPermission")
            if (knownAddresses.isNullOrEmpty()) {
                Log.d("wowEatersDataManager", "don't have known address")
                if (myLocation != null) {
                    LocationStatus(LocationStatusType.CURRENT_LOCATION, myLocation)
                } else {
                    LocationStatus(LocationStatusType.HAS_GPS_ENABLED_BUT_NO_LOCATION)
                }
            } else {
                Log.d("wowEatersDataManager", "has known address")
                if (myLocation != null) {
                    val closestAddress = GpsUtils().getClosestAddressToLocation(myLocation, knownAddresses)
                    if (closestAddress != null) {
                        Log.d("wowEatersDataManager", "using closest address: $closestAddress")
                        LocationStatus(LocationStatusType.KNOWN_LOCATION, closestAddress)
                    } else {
                        Log.d("wowEatersDataManager", "using current location: $myLocation")
                        LocationStatus(LocationStatusType.CURRENT_LOCATION, myLocation)
                    }
                } else {
                    LocationStatus(LocationStatusType.KNOWN_LOCATION_WITH_BANNER, knownAddresses[0])
                }
            }
        } else { // GPS permission denied
            Log.d("wowEatersDataManager", "don't have gpsPermission or Gps disabled")
            if (knownAddresses.isNullOrEmpty()) {
                LocationStatus(LocationStatusType.NO_GPS_ENABLED_AND_NO_LOCATION)
            } else {
                LocationStatus(LocationStatusType.KNOWN_LOCATION_WITH_BANNER, knownAddresses[0])
            }
        }
    }

//    fun getLocationData(): LocationLiveData {
//        return locationManager.getLocationData()
////        return locationManager.getFinalAddressLiveData()
//    }


    fun getFeedRequest(): FeedRequest {
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
        feedRequest.timestamp = deliveryTimeManager.getDeliveryTimestamp()

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
        return lastChosenAddress ?: getLocationStatus().address
    }

    fun setLastChosenAddress(address: Address?) {
            this.lastChosenAddress = address
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

    fun getCurrentPaymentMethod(): PaymentMethod? {
        return currentPaymentMethod
    }

    fun getCustomerCardId(): String {
        if (currentPaymentMethod != null) {
            return currentPaymentMethod?.id!!
        }
        return ""
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
        return isUserChooseSpecificAddress

    }

    fun getDropoffLocation(): String? {
        if (currentEater?.addresses != null && currentEater?.addresses!!.size > 0) {
            return currentEater?.addresses?.first()?.getDropoffLocationStr()
        } else {
            return getLastChosenAddress()?.getDropoffLocationStr()
        }
    }


    fun setUserChooseSpecificTime(hasSpecificTime: Boolean) {
//        this.hasSpecificTime = hasSpecificTime
    }

    var sid: String? = null
    var cid: String? = null
    fun setUserCampaignParam(sid: String? = null, cid: String? = null) {
        sid?.let {
            this.sid = it
        }
        cid?.let {
            this.cid = it
        }
    }


    companion object {
        const val TAG = "wowEaterAddressManager"
    }



}