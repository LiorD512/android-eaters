package com.bupp.wood_spoon_eaters.managers

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import com.bupp.wood_spoon_eaters.managers.location.LocationManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.EaterDataRepository
import com.bupp.wood_spoon_eaters.repositories.UserRepository


class EaterDataManager(
    val context: Context, private val locationManager: LocationManager, private val eventsManager: EventsManager,
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

    fun updateSelectedAddress(selectedAddress: Address?) {
        locationManager.setSelectedAddressAndUpdateParams(selectedAddress)
    }

    fun getLastChosenAddress(): Address? {
        return locationManager.getLastChosenAddress()
    }

    fun hasUserSetAnAddress(): Boolean {
        return getFinalAddressLiveDataParam().value?.id != null
    }

    fun stopLocationUpdates() {
        locationManager.forceStopLocationUpdates(true)
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
    private val traceableOrders = MutableLiveData<List<Order>?>()

    suspend fun checkForTraceableOrders() {
        val result = eaterDataRepository.getTraceableOrders()
        when (result.type) {
            EaterDataRepository.EaterDataRepoStatus.GET_TRACEABLE_SUCCESS -> {
                result.data?.let {
                    Log.d(TAG, "checkForTraceableOrders - success")
                    traceableOrdersList = it
                    traceableOrders.postValue(it)
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

    suspend fun cancelOrder(orderId: Long?, note: String?): EaterDataRepository.EaterDataRepoResult<Void>? {
        orderId?.let{
            val result = eaterDataRepository.cancelOrder(it, note)
            when (result.type) {
                EaterDataRepository.EaterDataRepoStatus.CANCEL_ORDER_SUCCESS -> {
                    result?.let {
                        Log.d(TAG, "checkForTraceableOrders - success")
                        checkForTraceableOrders()
                        return result
                    }
                }
                EaterDataRepository.EaterDataRepoStatus.CANCEL_ORDER_FAILED -> {
                    Log.d(TAG, "checkForTraceableOrders - failed")
                    return result
                }
                EaterDataRepository.EaterDataRepoStatus.WS_ERROR -> {
                    Log.d(TAG, "checkForTraceableOrders - es error")
                    return result

                }
                else -> {
                    return result
                }
            }
        }
        return null
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
                    Log.d(TAG, "refreshMyFavorites - success")
                    favoritesDishLiveData.postValue(it)
                    favoritesDishList = it
                }
            }
            EaterDataRepository.EaterDataRepoStatus.GET_FAVORITES_FAILED -> {
                Log.d(TAG, "refreshMyFavorites - failed")
                favoritesDishLiveData.postValue(listOf())
                favoritesDishList = null
            }
            EaterDataRepository.EaterDataRepoStatus.WS_ERROR -> {
                Log.d(TAG, "refreshMyFavorites - es error")

            }
            else -> {

            }
        }
    }

    fun getLastFeedRequest(): FeedRequest {
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

    fun getDeliveryTimestamp(): String? {
        return deliveryTimeManager.getDeliveryTimestamp()
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


    /////////////////////////////////////////
    ///////         Referrals         ///////
    /////////////////////////////////////////

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

    fun refreshSegment() {
        val curAddress = locationManager.getLastChosenAddress()
        eventsManager.initSegment(currentEater, curAddress)
    }


    /////////////////////////////////////////
    ////////         Events         /////////
    /////////////////////////////////////////

    fun logUxCamEvent(eventName: String, params: Map<String, String>? = null){
        eventsManager.logEvent(eventName, params)
    }



    companion object {
        const val TAG = "wowEaterDataManager"
    }


}