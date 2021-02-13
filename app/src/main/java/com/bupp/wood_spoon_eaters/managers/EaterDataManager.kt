package com.bupp.wood_spoon_eaters.managers

import android.content.Context
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import com.bupp.wood_spoon_eaters.managers.location.LocationManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import com.stripe.android.model.PaymentMethod


class EaterDataManager(val context: Context, private val locationManager: LocationManager,
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
    fun getLocationData() = locationManager.getLocationData()

    fun updateSelectedAddress(selectedAddress: Address) {
        locationManager.setSelectedAddressAndUpdateParams(selectedAddress)
    }



    /////////////////////////////////////////
    ///////////      FEED         ///////////
    /////////////////////////////////////////

    fun setDefaultFeedUi() {
        locationManager.setDefaultAddress()
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
//        this.isUserChooseSpecificAddress = isSpecificAddress
    }

    fun isUserChooseSpecificAddress(): Boolean {
        //is user didn't chose my location
        return false

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

    fun getDeliveryTimestamp(): String? {
        return deliveryTimeManager.getDeliveryTimestamp()
    }

    fun stopLocationUpdates() {
        locationManager.forceStopLocationUpdates(true)
    }


    companion object {
        const val TAG = "wowEaterAddressManager"
    }



}