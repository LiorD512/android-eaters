package com.bupp.wood_spoon_eaters.features.main.delivery_details

import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.utils.Utils
import java.util.*

class DeliveryDetailsViewModel(private val orderManager: OrderManager) : ViewModel() {

//    data class CurrentDataEvent(val address: Address?, val isDelivery: Boolean?)
    data class LastDataEvent(val address: Address?, val time: Date?)
    val lastDeliveryDetails: SingleLiveEvent<LastDataEvent> = SingleLiveEvent()

//    fun getCurrentDeliveryDetails(): CurrentDataEvent {
//        val address = orderManager.getLastOrderAddress()
//        val isDelivery = orderManager.isDelivery
//        return CurrentDataEvent(address, isDelivery)
//    }

    fun getLastDeliveryDetails() {
        val address = orderManager.getLastOrderAddress()
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




}
