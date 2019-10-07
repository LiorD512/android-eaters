package com.bupp.wood_spoon_eaters.features.address_and_location

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
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings
import com.bupp.wood_spoon_eaters.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddressChooserViewModel(val api: ApiService, val settings: AppSettings, val eaterDataManager: EaterDataManager): ViewModel(), EaterDataManager.EaterDataMangerListener {


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

    fun setNoAddress(){
        eaterDataManager.setUserChooseSpecificAddress(false)
        eaterDataManager.setLastChosenAddress(null)
    }

    fun getChosenAddress(): Address?{
        return eaterDataManager.getLastChosenAddress()
    }

    data class AddressUpdateEvent(val currentAddress: Address?)
    val addressUpdateEvent: SingleLiveEvent<AddressUpdateEvent> = SingleLiveEvent()
    override fun onAddressChanged(updatedAddress: Address?) {
        addressUpdateEvent.postValue(AddressUpdateEvent(updatedAddress))
    }

//    fun startLocationUpdates(activity: Activity) {
//        if(!permissionManager.hasPermission(activity, Constants.FINE_LOCATION_PERMISSION) || !permissionManager.hasPermission(activity, Constants.COARSE_LOCATION_PERMISSION)){
//            Log.d(TAG, "request permission")
//            permissionManager.requestPermission(activity, arrayOf(Constants.FINE_LOCATION_PERMISSION, Constants.COARSE_LOCATION_PERMISSION), Constants.LOCATION_PERMISSION_REQUEST_CODE)
//        }else{
//            Log.d(TAG, "location setting success")
//            eaterDataManager.startLocationUpdates()
//        }
//    }
//
//    fun stopLocationUpdates() {
//        eaterDataManager.stopLocationUpdates()
//    }
//
//    fun getCurrentAddress(): Address?{
//        val currentAddress = eaterDataManager.getLastChosenAddress()
//        if(currentAddress == null){
//            eaterDataManager.setLocationListener(this)
//        }
//        return currentAddress
//    }






}