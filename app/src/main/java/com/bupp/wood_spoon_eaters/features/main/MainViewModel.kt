package com.bupp.wood_spoon_eaters.features.main

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.fcm.FcmManager
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.*
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings
import com.bupp.wood_spoon_eaters.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(val api: ApiService, val settings: AppSettings, val permissionManager:PermissionManager, val orderManager: OrderManager,
                    val eaterDataManager: EaterDataManager, val fcmManager: FcmManager
): ViewModel(), EaterDataManager.EaterDataMangerListener {


    var waitingForAddressAction: Boolean = false
    private var hasPendingOrder: Boolean = false
    private var hasActiveOrder: Boolean = false
    val addressUpdateActionEvent: SingleLiveEvent<AddressUpdateEvent> = SingleLiveEvent()
    val addressUpdateEvent: SingleLiveEvent<AddressUpdateEvent> = SingleLiveEvent()
    data class AddressUpdateEvent(val currentAddress: Address?)

    private val TAG = "wowMainVM"

    fun initFcmListener(){
        fcmManager.initFcmListener()
    }

    fun getUserName(): String {
        return eaterDataManager.currentEater?.firstName!!
    }

    fun getLastOrderTime(): String? {
        return eaterDataManager.getFeedSearchTimeString()
    }

    fun startLocationUpdates(activity: Activity) {
        if(!permissionManager.hasPermission(activity, Constants.FINE_LOCATION_PERMISSION) || !permissionManager.hasPermission(activity, Constants.COARSE_LOCATION_PERMISSION)){
            Log.d(TAG, "request permission")
            permissionManager.requestPermission(activity, arrayOf(Constants.FINE_LOCATION_PERMISSION, Constants.COARSE_LOCATION_PERMISSION), Constants.LOCATION_PERMISSION_REQUEST_CODE)
        }else{
            Log.d(TAG, "location setting success")
            eaterDataManager.startLocationUpdates()
        }
    }

    fun stopLocationUpdates() {
        eaterDataManager.stopLocationUpdates()
    }

    fun getCurrentAddress(): Address?{
        val currentAddress = eaterDataManager.getLastChosenAddress()
        if(currentAddress == null){
            eaterDataManager.setLocationListener(this)
        }
        return currentAddress
    }

    fun getListOfAddresses(): ArrayList<Address>? {
        if(eaterDataManager.currentEater != null){
            return eaterDataManager.currentEater!!.addresses
        }
        return null
    }

    enum class NoLocationUiEvent{
        DEVICE_LOCATION_OFF,
        NO_LOCATIONS_SAVED
    }
    val noUserLocationEvent = SingleLiveEvent<NoLocationUiEvent>()
    override fun onLocationEmpty() {
        //this method fires when device location services is off
        if(getListOfAddresses() == null || getListOfAddresses()!!.isEmpty()){
            //if user never saved a location -> will show dialog
            noUserLocationEvent.postValue(NoLocationUiEvent.NO_LOCATIONS_SAVED)
        }
//        else{
//            getListOfAddresses()?.let{
//                eaterDataManager.setLastChosenAddress(it.get(0))
//                onAddressChanged(it.get(0))
//            }
//
//        }
    }

    override fun onUsingPreviousLocation() {
        noUserLocationEvent.postValue(NoLocationUiEvent.DEVICE_LOCATION_OFF)
    }


    override fun onAddressChanged(updatedAddress: Address?) {
        Log.d("wowMainVM","onAddressChanged")
        addressUpdateEvent.postValue(AddressUpdateEvent(updatedAddress))
        if(waitingForAddressAction){
            addressUpdateActionEvent.postValue(AddressUpdateEvent(updatedAddress))
        }
    }

    val checkCartStatus: SingleLiveEvent<CheckCartStatusEvent> = SingleLiveEvent()
    data class CheckCartStatusEvent(val hasPendingOrder: Boolean, val totalPrice: Double?)
    fun checkCartStatus() {
        hasPendingOrder = orderManager.haveCurrentActiveOrder()
        var totalPrice = 0.0
        if(hasPendingOrder){
            orderManager.curOrderResponse?.orderItems?.let{
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
                    if(activeOrders != null && activeOrders.size > 0){
                        hasActiveOrder = true
                        getActiveOrders.postValue(GetActiveOrdersEvent(true, activeOrders, showDialog))
                    }else{
                        hasActiveOrder = false
                        getActiveOrders.postValue(GetActiveOrdersEvent(false, null, showDialog))
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
                    if(trigger != null && trigger.shouldRateOrder != null){
                        getTriggers.postValue(GetTriggers(true, trigger))
                    }else{
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
        api.getCook(id).enqueue(object: Callback<ServerResponse<Cook>>{
            override fun onResponse(call: Call<ServerResponse<Cook>>, response: Response<ServerResponse<Cook>>) {
                if(response.isSuccessful){
                    val cook = response.body()?.data
                    Log.d("wowFeedVM","getCurrentCook success: ")
                    getCookEvent.postValue(CookEvent(true, cook))
                }else{
                    Log.d("wowFeedVM","getCurrentCook fail")
                    getCookEvent.postValue(CookEvent(false,null))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Cook>>, t: Throwable) {
                Log.d("wowFeedVM","getCurrentCook big fail")
                getCookEvent.postValue(CookEvent(false,null))
            }
        })
    }


    fun getCurrentEater(): Eater? {
        return eaterDataManager.currentEater
    }

    fun hasAddress(): Boolean {
        return eaterDataManager.getLastChosenAddress() != null
    }

    fun getAddressAndProcceed() {

    }

//    fun disableEventData() {
//        eaterDataManager.disableEventDate()
//    }


}