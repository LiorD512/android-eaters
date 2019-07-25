package com.bupp.wood_spoon_eaters.features.main

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.LocationManager
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.managers.PermissionManager
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings
import com.bupp.wood_spoon_eaters.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(val api: ApiService, val settings: AppSettings, val permissionManager:PermissionManager, val orderManager: OrderManager, val eaterDataManager: EaterDataManager): ViewModel(),
    EaterDataManager.EaterDataMangerListener {


    val addressUpdateEvent: SingleLiveEvent<AddressUpdateEvent> = SingleLiveEvent()
    data class AddressUpdateEvent(val currentAddress: Address?)



    private val TAG = "wowMainVM"
    fun isFirstTime(): Boolean{
        return settings.isFirstTime()
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
        return eaterDataManager.currentEater!!.addresses
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
        }else{
            //disable below line to ensure reUpdating location
            eaterDataManager.removeLocationListener(this)
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
                        getActiveOrders.postValue(GetActiveOrdersEvent(true, activeOrders))
                    }else{
                        getActiveOrders.postValue(GetActiveOrdersEvent(false, null))
                    }
                } else {
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

}