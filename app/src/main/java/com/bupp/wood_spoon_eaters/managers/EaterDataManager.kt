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
                       val deliveryTimeManager: DeliveryTimeManager, private val userRepository: UserRepository) {

    val currentEater: Eater?
    get() = userRepository.getUser()

    /////////////////////////////////////////
    ////////    DELIVERY_TIME       /////////
    /////////////////////////////////////////

    fun getDeliveryTimeLiveData() = deliveryTimeManager.getDeliveryTimeLiveData()

    /////////////////////////////////////////
    ///////////    LOCATION       ///////////
    /////////////////////////////////////////

    fun getFinalAddressLiveDataParam() = locationManager.getFinalAddressLiveDataParam()
    fun getFinalAddressLiveData() = locationManager.getFinalAddressLiveData() // used for feed !
    fun getLocationData() = locationManager.getLocationData()

    fun updateSelectedAddress(selectedAddress: Address) {
        locationManager.setSelectedAddressAndUpdateParams(selectedAddress)
    }

    fun getLocationStatus(): LocationStatus {
//        val finalAddress = locationManager.getFinalAddressLiveData().value //todo - ny
//        val knownAddresses = getListOfAddresses()
//        val myLocation = locationManager.getFinalAddressLiveData().value
//        val hasGpsPermission = appSettings.hasGPSPermission
//        val isGpsEnabled = true//locationManager.isGpsEnabled
//        Log.d(
//            "wowEatersDataManager",
//            "getLocationStatus: finalAddress: $finalAddress, hasGpsPermission: $hasGpsPermission, isGpsEnabled: $isGpsEnabled, knownAddresses: $knownAddresses, myLocation: $myLocation "
//        )
//        return if (hasGpsPermission && isGpsEnabled) {
//            Log.d("wowEatersDataManager", "has gpsPermission")
//            if (knownAddresses.isNullOrEmpty()) {
//                Log.d("wowEatersDataManager", "don't have known address")
//                if (myLocation != null) {
//                    LocationStatus(LocationStatusType.CURRENT_LOCATION, myLocation)
//                } else {
                    return LocationStatus(LocationStatusType.HAS_GPS_ENABLED_BUT_NO_LOCATION)
//                }
//            } else {
//                Log.d("wowEatersDataManager", "has known address")
//                if (myLocation != null) {
//                    val closestAddress = GpsUtils().getClosestAddressToLocation(myLocation, knownAddresses)
//                    if (closestAddress != null) {
//                        Log.d("wowEatersDataManager", "using closest address: $closestAddress")
//                        LocationStatus(LocationStatusType.KNOWN_LOCATION, closestAddress)
//                    } else {
//                        Log.d("wowEatersDataManager", "using current location: $myLocation")
//                        LocationStatus(LocationStatusType.CURRENT_LOCATION, myLocation)
//                    }
//                } else {
//                    LocationStatus(LocationStatusType.KNOWN_LOCATION_WITH_BANNER, knownAddresses[0])
//                }
//            }
//        } else { // GPS permission denied
//            Log.d("wowEatersDataManager", "don't have gpsPermission or Gps disabled")
//            if (knownAddresses.isNullOrEmpty()) {
//                LocationStatus(LocationStatusType.NO_GPS_ENABLED_AND_NO_LOCATION)
//            } else {
//                LocationStatus(LocationStatusType.KNOWN_LOCATION_WITH_BANNER, knownAddresses[0])
//            }
//        }
    }

    fun getFeedRequest(): FeedRequest {
        var feedRequest = FeedRequest()
        //address
//        val currentAddress = getLastChosenAddress()
//        if (isUserChooseSpecificAddress()) {
//            feedRequest.addressId = currentAddress?.id
//        } else {
//            feedRequest.lat = currentAddress?.lat
//            feedRequest.lng = currentAddress?.lng
//        }

        //time
        feedRequest.timestamp = deliveryTimeManager.getDeliveryTimestamp()

        return feedRequest
    }

//
//    interface EaterDataMangerListener {
//        fun onAddressChanged(currentAddress: Address?)
//        fun onLocationEmpty() {}
//        fun onUsingPreviousLocation() {}
//    }


    private fun getListOfAddresses(): List<Address>? {
        currentEater?.let {
            return it.addresses
        }
        return null
    }





    //Stripe customer card
    private var currentPaymentMethod: PaymentMethod? = null

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


    fun setUserChooseSpecificAddress(isSpecificAddress: Boolean) {
        this.isUserChooseSpecificAddress = isSpecificAddress
    }

    fun isUserChooseSpecificAddress(): Boolean {
        //is user didn't chose my location
        return isUserChooseSpecificAddress

    }

//    fun getDropoffLocation(): String? {
//        if (currentEater?.addresses != null && currentEater?.addresses!!.size > 0) {
//            return currentEater?.addresses?.first()?.getDropoffLocationStr()
//        } else {
//            return getLastChosenAddress()?.getDropoffLocationStr()
//        }
//    }


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