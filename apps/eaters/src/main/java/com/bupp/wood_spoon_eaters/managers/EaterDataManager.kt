package com.bupp.wood_spoon_eaters.managers

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.managers.location.LocationManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.EaterDataRepository
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class EaterDataManager(
    val context: Context, private val locationManager: LocationManager, private val eatersAnalyticsTracker: EatersAnalyticsTracker,
    private val userRepository: UserRepository, private val eaterDataRepository: EaterDataRepository
): LifecycleObserver {

    val currentEater: Eater?
        get() = userRepository.getUser()

    val currentEaterFlow = userRepository.currentEaterFlow

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    suspend fun refreshCurrentEater() {
        userRepository.initUserRepo()
    }


    /////////////////////////////////////////
    ///////////    LOCATION       ///////////
    /////////////////////////////////////////

    fun getFinalAddressLiveDataParam() = locationManager.getFinalAddressLiveDataParam()
    fun getLocationData() = locationManager.getLocationData()

    fun updateSelectedAddress(selectedAddress: Address?, addressType: LocationManager.AddressDataType? = null) {
        locationManager.setSelectedAddressAndUpdateParams(selectedAddress, addressType)
    }

    fun getLastChosenAddress(): Address? {
        return locationManager.getLastChosenAddress()
    }

    fun hasUserSetAnAddress(): Boolean {
        return getFinalAddressLiveDataParam().value?.id != null
    }

    fun hasUserSetDetails(): Boolean {
        return currentEater?.let {
            !it.firstName.isNullOrBlank()
                    && !it.lastName.isNullOrBlank()
                    && !it.email.isNullOrBlank()
                    && !it.phoneNumber.isNullOrBlank()
                    && it.phoneNumberVerified == true
        } ?: false
    }

    fun stopLocationUpdates() {
        locationManager.forceStopLocationUpdates(true)
    }

    fun rollBackToPreviousAddress() {
        locationManager.rollBackToPreviousAddress()
    }

    /**
     * this function is called when we receive device location when in SelectAddress Screen
     * we want to update user location to this device location when he has no other available address
     */
    fun updateLocationIfNeeded(addressRequest: AddressRequest) {
        val noAddressAvailable = currentEater?.addresses?.isNullOrEmpty() ?: true
        if(noAddressAvailable){
            updateSelectedAddress(addressRequest.toAddress(), LocationManager.AddressDataType.DEVICE_LOCATION)
        }



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

    suspend fun checkForTraceableOrders(): List<Order>? {
        val result = eaterDataRepository.getTraceableOrders()
        when (result.type) {
            EaterDataRepository.EaterDataRepoStatus.GET_TRACEABLE_SUCCESS -> {
                result.data?.let {
                    Log.d(TAG, "checkForTraceableOrders - success")
                    traceableOrdersList = it
                    traceableOrders.postValue(it)
                    return traceableOrdersList
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
        return null
    }

    suspend fun cancelOrder(orderId: Long?, note: String?): EaterDataRepository.EaterDataRepoResult<Any>? {
        orderId?.let{
            val result = eaterDataRepository.cancelOrder(it, note)
            when (result.type) {
                EaterDataRepository.EaterDataRepoStatus.CANCEL_ORDER_SUCCESS -> {
                    result?.let {
                        MTLogger.c(TAG, "cancelOrder - success")
                        checkForTraceableOrders()
                        return result
                    }
                }
                EaterDataRepository.EaterDataRepoStatus.CANCEL_ORDER_FAILED -> {
                    MTLogger.c(TAG, "cancelOrder - failed")
                    return result
                }
                EaterDataRepository.EaterDataRepoStatus.WS_ERROR -> {
                    MTLogger.c(TAG, "cancelOrder - es error")
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
    /////////      Triggers         /////////
    /////////////////////////////////////////

    fun getTriggers() = triggerEvent
    private val triggerEvent = LiveEventData<Trigger>()

    suspend fun checkForTriggers() {
        val result = eaterDataRepository.getTriggers()
        when (result.type) {
            EaterDataRepository.EaterDataRepoStatus.GET_TRIGGERS_SUCCESS -> {
                result.data?.let {
                    Log.d(TAG, "checkForTraceableOrders - success")
                    triggerEvent.postRawValue(it)
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
    ////////         Events         /////////
    /////////////////////////////////////////


    fun refreshSegment() {
        val curAddress = locationManager.getLastChosenAddress()
        eatersAnalyticsTracker.initSegment(currentEater, curAddress)
    }

    fun logUxCamEvent(eventName: String, params: Map<String, String>? = null) {
        eatersAnalyticsTracker.logEvent(eventName, params)
    }

    fun getCartAddressId(): Long? {
        return getLastChosenAddress()?.id
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppStart() {
        Log.d(TAG,"onAppStart")
        MainScope().launch {
            checkForTriggers()
        }
    }



    companion object {
        const val TAG = "wowEaterDataManager"
    }



}