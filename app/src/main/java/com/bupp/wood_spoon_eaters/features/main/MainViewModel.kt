package com.bupp.wood_spoon_eaters.features.main

import android.app.Activity
import android.util.Log
import androidx.core.util.Consumer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.fcm.FcmManager
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.*
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.common.AppSettings
import com.bupp.wood_spoon_eaters.common.Constants
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class MainViewModel(
    val api: ApiService, val settings: AppSettings, private val permissionManager: PermissionManager, val orderManager: OrderManager,
    val eaterDataManager: EaterDataManager, private val fcmManager: FcmManager, val eventsManager: EventsManager
) : ViewModel(), EaterDataManager.EaterDataMangerListener {

    val mainActHeaderEvent = MutableLiveData<MainActActionEvent>()
    data class MainActActionEvent(val time: String?, val address: Address?)

    init {
        eventsManager.initSegment()
        fcmManager.initFcmListener()
        initHeaderUi()
    }

    private fun initHeaderUi() {
        val lastSelectedTime = eaterDataManager.getFeedSearchTimeString()
        val currentAddress = eaterDataManager.getLastChosenAddress()
        mainActHeaderEvent.postValue(MainActActionEvent(lastSelectedTime, currentAddress))
    }

//    fun getLastOrderTime(): String? {
//        return eaterDataManager.getFeedSearchTimeString()
//    }
//
//    fun getCurrentAddress(): Address? {
//        val currentAddress = eaterDataManager.getLastChosenAddress()
//        if (currentAddress == null) {
//            eaterDataManager.setLocationListener(this)
//        }
//        return currentAddress
//    }


    var waitingForAddressAction: Boolean = false
    private var hasPendingOrder: Boolean = false
    private var hasActiveOrder: Boolean = false
    val addressUpdateActionEvent: SingleLiveEvent<AddressUpdateEvent> = SingleLiveEvent()
    val addressUpdateEvent: SingleLiveEvent<AddressUpdateEvent> = SingleLiveEvent()


    data class AddressUpdateEvent(val currentAddress: Address?)

    private val TAG = "wowMainVM"


    fun getUserName(): String {
        return eaterDataManager.currentEater?.firstName!!
    }



    fun startLocationUpdates() {
        eaterDataManager.startLocationUpdates()
//
//        if (permissionManager.hasPermission(activity, Constants.FINE_LOCATION_PERMISSION) || !permissionManager.hasPermission(
//                activity, Constants.COARSE_LOCATION_PERMISSION)) {
//            Log.d(TAG, "location setting success")
//        } else {
//            Log.d(TAG, "request permission")
//            requestLocationPermission(activity)
//        }
    }

    private fun requestLocationPermission(activity: Activity){
        permissionManager.requestPermission(
            activity,
            arrayOf(Constants.FINE_LOCATION_PERMISSION, Constants.COARSE_LOCATION_PERMISSION),
            Constants.LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    fun stopLocationUpdates() {
        eaterDataManager.stopLocationUpdates()
    }



    private fun getListOfAddresses(): ArrayList<Address>? {
        if (eaterDataManager.currentEater != null) {
            return eaterDataManager.currentEater!!.addresses
        }
        return null
    }

    enum class NoLocationUiEvent {
        DEVICE_LOCATION_OFF,
        NO_LOCATIONS_SAVED
    }

    val noUserLocationEvent = SingleLiveEvent<NoLocationUiEvent>()
    override fun onLocationEmpty() {
        //this method fires when device location services is off
        if (getListOfAddresses() == null || getListOfAddresses()!!.isEmpty()) {
            //if user never saved a location -> will show dialog
            noUserLocationEvent.postValue(NoLocationUiEvent.NO_LOCATIONS_SAVED)
        }
    }

    val locationSettingsEvent = SingleLiveEvent<Boolean>()
    fun startAndroidLocationSettings(){
        locationSettingsEvent.postValue(true)
    }

    override fun onUsingPreviousLocation() {
        noUserLocationEvent.postValue(NoLocationUiEvent.DEVICE_LOCATION_OFF)
    }


    override fun onAddressChanged(updatedAddress: Address?) {
        Log.d("wowMainVM", "onAddressChanged")
        addressUpdateEvent.postValue(AddressUpdateEvent(updatedAddress))
        if (waitingForAddressAction) {
            addressUpdateActionEvent.postValue(AddressUpdateEvent(updatedAddress))
        }
    }

    val checkCartStatus: SingleLiveEvent<CheckCartStatusEvent> = SingleLiveEvent()

    data class CheckCartStatusEvent(val hasPendingOrder: Boolean, val totalPrice: Double?)

    fun checkCartStatus() {
        hasPendingOrder = orderManager.haveCurrentActiveOrder()
        var totalPrice = 0.0
        if (hasPendingOrder) {
            orderManager.curOrderResponse?.orderItems?.let {
                it.forEach {
                    totalPrice += (it.price?.value * it.quantity)
                }
            }
//            totalPrice = orderManager.curOrderResponse?.total?.value
        }
        checkCartStatus.postValue(CheckCartStatusEvent(hasPendingOrder, totalPrice))
    }

    val getActiveOrders: SingleLiveEvent<GetActiveOrdersEvent> = SingleLiveEvent()

    data class GetActiveOrdersEvent(val isSuccess: Boolean, val orders: ArrayList<Order>?, val showDialog: Boolean = false)

    fun checkForActiveOrder(showDialog: Boolean = false) {
        api.getTrackableOrders().enqueue(object : Callback<ServerResponse<ArrayList<Order>>> {
            override fun onResponse(call: Call<ServerResponse<ArrayList<Order>>>, response: Response<ServerResponse<ArrayList<Order>>>) {
                if (response.isSuccessful) {
//                    Log.d("wow", "phoneNumber sent: $phoneNumber")
                    Log.d("wowMainVM", "getTrackableOrders success")
                    val activeOrders = response.body()!!.data
                    if (activeOrders != null && activeOrders?.size > 0) {
                        hasActiveOrder = true
                        getActiveOrders.postValue(GetActiveOrdersEvent(true, activeOrders, showDialog))
                    } else {
                        hasActiveOrder = false
                        getActiveOrders.postValue(GetActiveOrdersEvent(false, null, showDialog))
                    }
                    if(showDialog){
                        eventsManager.logUxCamEvent(Constants.UXCAM_EVENT_TRACK_ORDER_CLICK)
                    }
                } else {
                    hasActiveOrder = false
                    Log.d("wowMainVM", "getTrackableOrders fail")
                    getActiveOrders.postValue(GetActiveOrdersEvent(false, null))
                }
            }

            override fun onFailure(call: Call<ServerResponse<ArrayList<Order>>>, t: Throwable) {
                Log.d("wowMainVM", "getTrackableOrders big fail")
                getActiveOrders.postValue(GetActiveOrdersEvent(false, null))
            }
        })
    }

    val getTriggers: SingleLiveEvent<GetTriggers> = SingleLiveEvent()

    data class GetTriggers(val isSuccess: Boolean, val trigger: Trigger?)

    fun checkForTriggers() {
        api.getTriggers().enqueue(object : Callback<ServerResponse<Trigger>> {
            override fun onResponse(call: Call<ServerResponse<Trigger>>, response: Response<ServerResponse<Trigger>>) {
                if (response.isSuccessful) {
                    Log.d("wowMainVM", "getTriggers success")
                    val trigger = response.body()!!.data
                    if (trigger != null && trigger.shouldRateOrder != null) {
                        getTriggers.postValue(GetTriggers(true, trigger))
                    } else {
                        getTriggers.postValue(GetTriggers(false, null))
                    }
                } else {
                    Log.d("wowMainVM", "getTriggers fail")
                    getActiveOrders.postValue(GetActiveOrdersEvent(false, null))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Trigger>>, t: Throwable) {
                Log.d("wowMainVM", "getTriggers big fail")
                getActiveOrders.postValue(GetActiveOrdersEvent(false, null))
            }
        })
    }

    val getCookEvent: SingleLiveEvent<CookEvent> = SingleLiveEvent()

    data class CookEvent(val isSuccess: Boolean = false, val cook: Cook?)

    fun getCurrentCook(id: Long) {
        val currentAddress = eaterDataManager.getLastChosenAddress()
        api.getCook(cookId = id, lat = currentAddress?.lat, lng = currentAddress?.lng).enqueue(object : Callback<ServerResponse<Cook>> {
            override fun onResponse(call: Call<ServerResponse<Cook>>, response: Response<ServerResponse<Cook>>) {
                if (response.isSuccessful) {
                    val cook = response.body()?.data
                    Log.d("wowFeedVM", "getCurrentCook success: ")
                    getCookEvent.postValue(CookEvent(true, cook))
                } else {
                    Log.d("wowFeedVM", "getCurrentCook fail")
                    getCookEvent.postValue(CookEvent(false, null))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Cook>>, t: Throwable) {
                Log.d("wowFeedVM", "getCurrentCook big fail")
                getCookEvent.postValue(CookEvent(false, null))
            }
        })
    }


    fun getCurrentEater(): Eater? {
        return eaterDataManager.currentEater
    }

    fun hasAddress(): Boolean {
        return eaterDataManager.getLastChosenAddress() != null
    }

    fun initLocationFalse() {
        eaterDataManager.onLocationEmpty()
    }

    val getUserCampaignDataEvent: SingleLiveEvent<Campaign?> = SingleLiveEvent()
    fun checkForUserCampaignData() {
//        getUserCampaignDataEvent.postValue(eaterDataManager.currentEater)
    }


    val getShareCampaignEvent: SingleLiveEvent<Campaign?> = SingleLiveEvent()
    fun checkForShareCampaign() {
        api.getCurrentShareCampaign().enqueue(object : Callback<ServerResponse<Campaign>> {
            //check server for active sharing campaign for eater
            override fun onResponse(call: Call<ServerResponse<Campaign>>, response: Response<ServerResponse<Campaign>>) {
                if (response.isSuccessful) {
                    val campaign = response.body()?.data
                    Log.d("wowMainVM", "getCurrentShareCampaign success: ")
                    getShareCampaignEvent.postValue(campaign)
                } else {
                    Log.d("wowMainVM", "getCurrentShareCampaign fail")
                    getShareCampaignEvent.postValue(null)
                }
            }

            override fun onFailure(call: Call<ServerResponse<Campaign>>, t: Throwable) {
                Log.d("wowMainVM", "getCurrentShareCampaign big fail")
                getShareCampaignEvent.postValue(null)
            }
        })
    }


    val activeCampaignEvent = SingleLiveEvent<ActiveCampaign?>()
    fun checkForCampaignReferrals() {
        //checks if user have an active campaign coupon prize
        val sid = eaterDataManager.sid
        val cid = eaterDataManager.cid
        if(sid != null){
            Log.d("wowMainVM", "init start")
            var serverCallMap = mutableMapOf<Int, Observable<*>>()
            serverCallMap.put(0, api.postCampaignReferrals(sid, cid))
//            serverCallMap.put(1, api.getMe()) //restore this ny.
            val requests = ArrayList<Observable<*>>()
            for (call in serverCallMap) {
                requests.add(call.value)
            }

            Observable.zip(requests) { objects ->
                Log.d("wowMainVM", "Observable success")

                //parse client
                val eaterServerResponse = objects[1] as ServerResponse<Eater>
                val eater: Eater? = eaterServerResponse.data
                eaterDataManager.currentEater = eater
                eater?.activeCampaign?.let{
                    activeCampaignEvent.postValue(it)
                    eaterDataManager.sid = null
                    eaterDataManager.cid = null
                }

                Log.d("wowMainVM", "eater parsing success: " + eater?.id)


                Any()
            }.timeout(55000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    object : Consumer<Any> {
                        override fun accept(p0: Any) {
                            Log.d("wowSplash", "Observable accept success")
                        }
                    }
                }, { result -> Log.d("wowSplash", "wowException $result") })
        }else{
            eaterDataManager.currentEater?.activeCampaign.let{
                activeCampaignEvent.postValue(it)
            }
        }
    }

    fun refreshUserData() {
        api.getMeCall().enqueue(object : Callback<ServerResponse<Eater>> {
            override fun onResponse(call: Call<ServerResponse<Eater>>, response: Response<ServerResponse<Eater>>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "on success! ")
                    var eater = response.body()?.data!!
                    eaterDataManager.currentEater = eater
                    checkForCampaignReferrals()
                } else {
                    Log.d(TAG, "on Failure! ")
                }
            }

            override fun onFailure(call: Call<ServerResponse<Eater>>, t: Throwable) {
                Log.d(TAG, "on big Failure! " + t.message)
            }
        })
    }

    fun resetOrderTimeIfNeeded() {
        if(!eaterDataManager.hasSpecificTime){
            eaterDataManager.orderTime = null
        }
    }

    val refreshAppDataEvent = SingleLiveEvent<Boolean>()
    fun checkIfMemoryCleaned() {
        if(eaterDataManager.currentEater == null){
            refreshAppDataEvent.postValue(true)
        }
    }


//    fun disableEventData() {
//        eaterDataManager.disableEventDate()
//    }


}