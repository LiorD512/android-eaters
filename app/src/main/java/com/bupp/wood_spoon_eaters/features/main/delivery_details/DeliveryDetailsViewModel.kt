package com.bupp.wood_spoon_eaters.features.main.delivery_details

import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.utils.AppSettings
import java.util.*

class DeliveryDetailsViewModel(val settings: AppSettings, private val orderManager: OrderManager, val eaterDataManager: EaterDataManager) : ViewModel(){//}, LocationManager.LocationManagerListener {

    //    data class CurrentDataEvent(val address: Address?, val isDelivery: Boolean?)
    data class LastDataEvent(val address: Address?, val time: Date?)
    val lastDeliveryDetails: SingleLiveEvent<LastDataEvent> = SingleLiveEvent()

    fun getLastDeliveryDetails() {
        val address = eaterDataManager.getLastChosenAddress()
        val time = eaterDataManager.getFeedSearchTime()
        lastDeliveryDetails.postValue(LastDataEvent(address, time))
    }

    fun setDeliveryTime(time: Date) {
        eaterDataManager.setUserChooseSpecificTime(true)
        eaterDataManager.orderTime = time
    }

    fun setDeliveryTimeAsap() {
        eaterDataManager.setUserChooseSpecificTime(false)
        eaterDataManager.orderTime = null
    }

//    fun getDeliveryTime(): String? {
//        if(eaterDataManager.orderTime != null){
//            return Utils.parseTime(eaterDataManager.orderTime)
//        }
//        return "ASAP"
//    }

    fun getListOfAddresses(): ArrayList<Address>? {
        return eaterDataManager.currentEater!!.addresses
    }

    fun getChosenAddress(): Address?{
        return eaterDataManager.getLastChosenAddress()
    }

//    fun initLocationListener() {
//        locationManager.setLocationManagerListener(this)
//    }
//
//    override fun onLocationChanged(mLocation: Location?) {
//
//    }



}
