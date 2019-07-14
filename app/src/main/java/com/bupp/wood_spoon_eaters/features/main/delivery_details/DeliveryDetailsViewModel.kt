package com.bupp.wood_spoon_eaters.features.main.delivery_details

import android.location.Location
import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterAddressManager
import com.bupp.wood_spoon_eaters.managers.LocationManager
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.utils.AppSettings
import com.bupp.wood_spoon_eaters.utils.Utils
import java.util.*

class DeliveryDetailsViewModel(val settings: AppSettings, private val orderManager: OrderManager, val eaterAddressManager: EaterAddressManager) : ViewModel(){//}, LocationManager.LocationManagerListener {

    //    data class CurrentDataEvent(val address: Address?, val isDelivery: Boolean?)
    data class LastDataEvent(val address: Address?, val time: Date?)
    val lastDeliveryDetails: SingleLiveEvent<LastDataEvent> = SingleLiveEvent()

//    fun getCurrentDeliveryDetails(): CurrentDataEvent {
//        val address = orderManager.getLastOrderAddress()
//        val isDelivery = orderManager.isDelivery
//        return CurrentDataEvent(address, isDelivery)
//    }

    fun getLastDeliveryDetails() {
        val address = eaterAddressManager.getLastChosenAddress()
        val time = orderManager.getLastOrderTime()
        lastDeliveryDetails.postValue(LastDataEvent(address, time))
    }

    fun setDeliveryTime(time: Date) {
        orderManager.updateOrder(orderTime = time)
    }

    fun getDeliveryTime(): String? {
        if(orderManager.orderTime != null){
            return Utils.parseTime(orderManager.orderTime)
        }
        return "ASAP"
    }

    fun getListOfAddresses(): ArrayList<Address>? {
        return settings.currentEater!!.addresses
    }

    fun getChosenAddress(): Address?{
        return eaterAddressManager.getLastChosenAddress()
    }

//    fun initLocationListener() {
//        locationManager.setLocationManagerListener(this)
//    }
//
//    override fun onLocationChanged(mLocation: Location?) {
//
//    }



}
