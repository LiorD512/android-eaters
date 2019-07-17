package com.bupp.wood_spoon_eaters.features.main

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterAddressManager
import com.bupp.wood_spoon_eaters.managers.LocationManager
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.managers.PermissionManager
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.utils.AppSettings
import com.bupp.wood_spoon_eaters.utils.Constants

class MainViewModel(val settings: AppSettings, val permissionManager:PermissionManager, val orderManager: OrderManager, val eaterAddressManager: EaterAddressManager): ViewModel(),
    LocationManager.LocationManagerListener {

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
        return settings.currentEater?.firstName!!
    }

    fun getLastOrderAddress(): String? {
        return ""
//        return orderManager.getLastOrderAddress()?.streetLine1
    }

    fun getLastOrderTime(): String? {
        return orderManager.getLastOrderTimeString()
    }

    fun getListOfAddresses(): ArrayList<Address>? {
        return settings.currentEater!!.addresses
    }

    fun setChosenAddress(address: Address){
        settings.setUserChooseSpecificAddress(true)
        eaterAddressManager.setLastChosenAddress(address)
    }

    fun getChosenAddress(): Address?{
        return eaterAddressManager.getLastChosenAddress()
    }

    fun startLocationUpdates(activity: Activity) {
        if(!permissionManager.hasPermission(activity, Constants.FINE_LOCATION_PERMISSION) || !permissionManager.hasPermission(activity, Constants.COARSE_LOCATION_PERMISSION)){
            Log.d(TAG, "request permission")
            permissionManager.requestPermission(activity, arrayOf(Constants.FINE_LOCATION_PERMISSION, Constants.COARSE_LOCATION_PERMISSION), Constants.LOCATION_PERMISSION_REQUEST_CODE)
        }else{
            Log.d(TAG, "location setting success")
            eaterAddressManager.startLocationUpdates()
        }
    }

    fun stopLocationUpdates() {
        eaterAddressManager.stopLocationUpdates()
    }

    fun getCurrentAddress(): Address?{
        val currentAddress = eaterAddressManager.getLastChosenAddress()
        if(currentAddress == null){
            eaterAddressManager.setLocationListener(this)
        }else{
            //disable below line to ensure reUpdating location
            eaterAddressManager.removeLocationListener(this)
        }
        return currentAddress
    }

    override fun onLocationChanged(updatedAddress: Address) {
        addressUpdateEvent.postValue(AddressUpdateEvent(updatedAddress))
    }

    fun loadCurrentSingleDishDetails() {
//        val id = orderManager.getCurrent
    }

}