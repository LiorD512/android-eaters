package com.bupp.wood_spoon_eaters.features.main.checkout

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService

class CheckoutViewModel(val api: ApiService, val orderManager:OrderManager) : ViewModel() {

    val getOrderDetailsEvent: SingleLiveEvent<OrderDetailsEvent> = SingleLiveEvent()

    data class OrderDetailsEvent(val order: Order?)

    fun getOrderDetails(){
        getOrderDetailsEvent.postValue(OrderDetailsEvent(orderManager.curOrderResponse))
    }

//    fun getLastOrderAddress(): String? {
//        return orderManager.getLastOrderAddress()?.streetLine1
//    }

}
