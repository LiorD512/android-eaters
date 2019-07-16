package com.bupp.wood_spoon_eaters.dialogs

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.Constants

class TrackOrderViewModel(val api: ApiService, val orderManager: OrderManager) : ViewModel() {



    val orderDetails: SingleLiveEvent<OrderDetailsEvent> = SingleLiveEvent()

    data class OrderDetailsEvent(/*val order: Order,*/val orderProgress: Int, val arrivalTime:String, val isNewMsgs:Boolean)

    fun getDumbOrderDetails() {
        orderDetails.postValue(OrderDetailsEvent(orderProgress=Constants.ORDER_PROGRESS_ORDER_IN_DELIVERY,arrivalTime = "16:21 PM",isNewMsgs = true))
    }
}