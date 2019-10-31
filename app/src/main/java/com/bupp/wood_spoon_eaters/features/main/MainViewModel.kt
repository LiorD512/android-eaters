package com.bupp.wood_spoon_eaters.features.main

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
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
                    val eaterDataManager: EaterDataManager, val fcmManager: FcmManager): ViewModel(), EaterDataManager.EaterDataMangerListener {


    private var hasPendingOrder: Boolean = false
    private var hasActiveOrder: Boolean = false
    val addressUpdateEvent: SingleLiveEvent<AddressUpdateEvent> = SingleLiveEvent()
    data class AddressUpdateEvent(val currentAddress: Address?)

    private val TAG = "wowMainVM"
    fun isFirstTime(): Boolean{
        return settings.isFirstTime()
    }

    fun initFcmListener(){
        fcmManager.initFcmListener()
    }

    fun checkPermission(context: Context, permissions: Array<String>): Boolean {
        for (item in permissions) {
            if (!permissionManager.hasPermission(context, item))
                return false
        }
        return true
    }

    fun requestPermission(activity: FragmentActivity, types: Array<String>, requestName: Int) {
        ActivityCompat.requestPermissions(activity, types, requestName)
    }

    fun getUserName(): String {
        return eaterDataManager.currentEater?.firstName!!
    }

    fun getLastOrderAddress(): String? {
        return ""
//        return orderManager.getLastOrderAddress()?.streetLine1
    }

    fun getLastOrderTime(): String? {
        return eaterDataManager.getLastOrderTimeString()
    }

    fun getListOfAddresses(): ArrayList<Address>? {
        if(eaterDataManager.currentEater != null){
            return eaterDataManager.currentEater!!.addresses
        }
        return arrayListOf()
    }

    fun setChosenAddress(address: Address){
        eaterDataManager.setUserChooseSpecificAddress(true)
        eaterDataManager.setLastChosenAddress(address)
    }

    fun getChosenAddress(): Address?{
        return eaterDataManager.getLastChosenAddress()
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


    override fun onAddressChanged(updatedAddress: Address?) {
        addressUpdateEvent.postValue(AddressUpdateEvent(updatedAddress))
    }

    fun loadCurrentSingleDishDetails() {
//        val id = orderManager.getCurrent
    }

    val getActiveOrders: SingleLiveEvent<GetActiveOrdersEvent> = SingleLiveEvent()
    data class GetActiveOrdersEvent(val isSuccess: Boolean, val orders: ArrayList<Order>?)

    fun checkForActiveOrder() {
        api.getTrackableOrders().enqueue(object : Callback<ServerResponse<ArrayList<Order>>> {
            override fun onResponse(call: Call<ServerResponse<ArrayList<Order>>>, response: Response<ServerResponse<ArrayList<Order>>>) {
                if (response.isSuccessful) {
//                    Log.d("wow", "phoneNumber sent: $phoneNumber")
                    Log.d("wowMainVM", "getTrackableOrders success")
                    val activeOrders = response.body()!!.data
                    if(activeOrders != null && activeOrders.size > 0){
                        hasActiveOrder = true
                        getActiveOrders.postValue(GetActiveOrdersEvent(true, activeOrders))
                    }else{
                        hasActiveOrder = false
                        getActiveOrders.postValue(GetActiveOrdersEvent(false, null))
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

    val checkCartStatus: SingleLiveEvent<CheckCartStatusEvent> = SingleLiveEvent()
    data class CheckCartStatusEvent(val hasPendingOrder: Boolean)
    fun checkCartStatus() {
        hasPendingOrder = orderManager.haveCurrentActiveOrder()
        checkCartStatus.postValue(CheckCartStatusEvent(hasPendingOrder))
    }

//    val getCookEvent: SingleLiveEvent<CookEvent> = SingleLiveEvent()
//    data class CookEvent(val isSuccess: Boolean = false, val cook: Cook?)
//    fun getCurrentCook(id: Long) {
//        api.getCook(id).enqueue(object: Callback<ServerResponse<Cook>>{
//            override fun onResponse(call: Call<ServerResponse<Cook>>, response: Response<ServerResponse<Cook>>) {
//                if(response.isSuccessful){
//                    val cook = response.body()?.data
//                    Log.d("wowFeedVM","getCurrentCook success: ")
//                    getCookEvent.postValue(CookEvent(true, cook))
//                }else{
//                    Log.d("wowFeedVM","getCurrentCook fail")
//                    getCookEvent.postValue(CookEvent(false,null))
//                }
//            }
//
//            override fun onFailure(call: Call<ServerResponse<Cook>>, t: Throwable) {
//                Log.d("wowFeedVM","getCurrentCook big fail")
//                getCookEvent.postValue(CookEvent(false,null))
//            }
//        })
//    }

    fun getCurrentEater(): Eater? {
        return eaterDataManager.currentEater
    }

    fun getContainerPaddingBottom(): Int{
        fun Int.DptoPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
        var padding = 0
        if(hasActiveOrder){
            padding += 55.DptoPx()
        }
        if(hasPendingOrder){
            padding += 55.DptoPx()
        }
        return padding
    }


}