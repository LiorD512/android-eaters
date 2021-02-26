package com.bupp.wood_spoon_eaters.managers

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import com.bupp.wood_spoon_eaters.managers.location.LocationManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.EaterDataRepository
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import com.stripe.android.model.PaymentMethod


class EaterDataManager(
    val context: Context, private val locationManager: LocationManager,
    private val deliveryTimeManager: DeliveryTimeManager, private val userRepository: UserRepository, private val eaterDataRepository: EaterDataRepository
) {

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

    fun getLastChosenAddress(): Address? {
        return locationManager.getLastChosenAddress()
    }

    fun hasUserSetAnAddress(): Boolean {
        return getFinalAddressLiveDataParam().value?.id != null
    }

    /////////////////////////////////////////
    ///////////      FEED         ///////////
    /////////////////////////////////////////

    fun setDefaultFeedUi() {
        locationManager.setDefaultAddress()
    }


    /////////////////////////////////////////
    /////      Traceable Orders         /////
    /////////////////////////////////////////

    var traceableOrdersList: List<Order>? = null
    fun getTraceableOrders() = traceableOrders
    private val traceableOrders = MutableLiveData<List<Order>>()

    suspend fun checkForTraceableOrders() {
        val result = eaterDataRepository.getTraceableOrders()
        when (result.type) {
            EaterDataRepository.EaterDataRepoStatus.GET_TRACEABLE_SUCCESS -> {
                result.data?.let {
                    if(it.isNotEmpty()){
                        Log.d(TAG, "checkForTraceableOrders - success")
                        traceableOrdersList = it
                        traceableOrders.postValue(it)
                    }
                }
            }
            EaterDataRepository.EaterDataRepoStatus.GET_TRACEABLE_FAILED -> {
                Log.d(TAG, "checkForTraceableOrders - failed")
            }
            EaterDataRepository.EaterDataRepoStatus.WS_ERROR -> {
                Log.d(TAG, "checkForTraceableOrders - es error")

            }
            else -> {

            }
        }
    }

    /////////////////////////////////////////
    /////////      Favorites         ////////
    /////////////////////////////////////////

    var favoritesDishList: List<Dish>? = null
    fun getFavoritesLiveData() = favoritesDishLiveData
    private val favoritesDishLiveData = MutableLiveData<List<Dish>>()

    suspend fun refreshMyFavorites() {
        val result = eaterDataRepository.getFavorites(getLastFeedRequest())
        when (result.type) {
            EaterDataRepository.EaterDataRepoStatus.GET_FAVORITES_SUCCESS -> {
                result.data?.let {
                    Log.d(TAG, "checkForTraceableOrders - success")
                    favoritesDishLiveData.postValue(it)
                    favoritesDishList = it
                }
            }
            EaterDataRepository.EaterDataRepoStatus.GET_FAVORITES_FAILED -> {
                Log.d(TAG, "checkForTraceableOrders - failed")
            }
            EaterDataRepository.EaterDataRepoStatus.WS_ERROR -> {
                Log.d(TAG, "checkForTraceableOrders - es error")

            }
            else -> {

            }
        }
    }

    private fun getLastFeedRequest(): FeedRequest {
        //being used in NewOrderActivity, uses params to init new Order.
        var feedRequest = FeedRequest()
        val lastAddress = getFinalAddressLiveDataParam().value
        lastAddress?.let {
            //address
            if (lastAddress.id != null) {
                feedRequest.addressId = lastAddress.id
            } else {
                feedRequest.lat = lastAddress.lat
                feedRequest.lng = lastAddress.lng
            }
        }

        //time
        feedRequest.timestamp = getDeliveryTimestamp()

        return feedRequest
    }


    /////////////////////////////////////////
    /////////      Triggers         /////////
    /////////////////////////////////////////

    fun getTriggers() = triggerEvent
    private val triggerEvent = MutableLiveData<Trigger>()

    suspend fun checkForTriggers() {
        val result = eaterDataRepository.getTriggers()
        when (result.type) {
            EaterDataRepository.EaterDataRepoStatus.GET_TRIGGERS_SUCCESS -> {
                result.data?.let {
                    Log.d(TAG, "checkForTraceableOrders - success")
                    triggerEvent.postValue(it)
                }
            }
            EaterDataRepository.EaterDataRepoStatus.GET_TRIGGERS_FAILED -> {
                Log.d(TAG, "checkForTraceableOrders - failed")
            }
            EaterDataRepository.EaterDataRepoStatus.WS_ERROR -> {
                Log.d(TAG, "checkForTraceableOrders - es error")

            }
            else -> {

            }
        }
    }




















//    //Stripe customer card
//    private var currentPaymentMethod: PaymentMethod? = null
//
//    fun updateCustomerCard(paymentMethod: PaymentMethod) {
//        this.currentPaymentMethod = paymentMethod
//    }
//
//    fun getCurrentPaymentMethod(): PaymentMethod? {
//        return currentPaymentMethod
//    }
//
//    fun getCustomerCardId(): String {
//        if (currentPaymentMethod != null) {
//            return currentPaymentMethod?.id!!
//        }
//        return ""
//    }
//
//
////    eater params and data
////
//
//
//    private var isUserChooseSpecificAddress: Boolean = false
//
////
//    fun setUserChooseSpecificAddress(isSpecificAddress: Boolean) {
////        this.isUserChooseSpecificAddress = isSpecificAddress
//    }
//
//    fun isUserChooseSpecificAddress(): Boolean {
//        //is user didn't chose my location
//        return false
//
//    }
//
//    fun getDropoffLocation(): String? {
//        if (currentEater?.addresses != null && currentEater?.addresses!!.size > 0) {
//            return currentEater?.addresses?.first()?.getDropoffLocationStr()
//        } else {
//            return getLastChosenAddress()?.getDropoffLocationStr()
//        }
//    }
//
//
//    fun setUserChooseSpecificTime(hasSpecificTime: Boolean) {
////        this.hasSpecificTime = hasSpecificTime
//    }

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
        const val TAG = "wowEaterDataManager"
    }


}