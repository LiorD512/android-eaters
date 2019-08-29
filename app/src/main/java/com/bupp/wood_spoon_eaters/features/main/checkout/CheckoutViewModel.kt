//package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.checkout
//
//import androidx.lifecycle.ViewModel
//import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
//import com.bupp.wood_spoon_eaters.managers.EaterDataManager
//import com.bupp.wood_spoon_eaters.managers.OrderManager
//import com.bupp.wood_spoon_eaters.model.*
//import com.bupp.wood_spoon_eaters.network.ApiService
//import java.util.*
//
//class CheckoutViewModel(val api: ApiService, val orderManager:OrderManager, val eaterDataManager: EaterDataManager) : ViewModel() {
//
//    val getDeliveryDetailsEvent: SingleLiveEvent<DeliveryDetailsEvent> = SingleLiveEvent()
//    val getOrderDetailsEvent: SingleLiveEvent<OrderDetailsEvent> = SingleLiveEvent()
//
//    data class DeliveryDetailsEvent(val address: Address?, val time: String?)
//    data class OrderDetailsEvent(val order: Order?)
//
//    fun getOrderDetails(){
//        getOrderDetailsEvent.postValue(OrderDetailsEvent(orderManager.curOrderResponse))
//    }
//
//    fun updateOrderAddress() {
//        orderManager.updateOrderRequest(deliveryAddress = eaterDataManager.getLastChosenAddress())
//    }
//
//    fun getDeliveryDetails() {
//        val address = eaterDataManager.getLastChosenAddress()
//        val time = eaterDataManager.getLastOrderTimeString()
//        getDeliveryDetailsEvent.postValue(DeliveryDetailsEvent(address, time))
//
//    }
//
//
//}
