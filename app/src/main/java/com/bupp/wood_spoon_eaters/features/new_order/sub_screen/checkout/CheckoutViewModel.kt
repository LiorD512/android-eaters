package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.checkout

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckoutViewModel(val api: ApiService, val orderManager:OrderManager, val eaterDataManager: EaterDataManager) : ViewModel() {

    val getDeliveryDetailsEvent: SingleLiveEvent<DeliveryDetailsEvent> = SingleLiveEvent()
    val getOrderDetailsEvent: SingleLiveEvent<OrderDetailsEvent> = SingleLiveEvent()

    data class DeliveryDetailsEvent(val address: Address?, val time: String?)
    data class OrderDetailsEvent(val order: Order?)

    val checkoutOrderEvent: SingleLiveEvent<CheckoutEvent> = SingleLiveEvent()
    val finalizeOrderEvent: SingleLiveEvent<FinalizeEvent> = SingleLiveEvent()
    data class CheckoutEvent(val isSuccess: Boolean)
    data class FinalizeEvent(val isSuccess: Boolean)

    fun getOrderDetails(){
        getOrderDetailsEvent.postValue(OrderDetailsEvent(orderManager.curOrderResponse))
    }

    fun updateOrderAddress() {
        orderManager.updateOrderRequest(deliveryAddress = eaterDataManager.getLastChosenAddress())
    }

    fun getDeliveryDetails() {
        val address = eaterDataManager.getLastChosenAddress()
        val time = eaterDataManager.getLastOrderTimeString()
        getDeliveryDetailsEvent.postValue(DeliveryDetailsEvent(address, time))

    }

    fun checkoutOrder(orderId: Long) {
        api.checkoutOrder(orderId).enqueue(object: Callback<ServerResponse<Void>>{
            override fun onResponse(call: Call<ServerResponse<Void>>, response: Response<ServerResponse<Void>>) {
                if(response.isSuccessful){
                    checkoutOrderEvent.postValue(CheckoutEvent(true))
                }else{
                    checkoutOrderEvent.postValue(CheckoutEvent(false))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Void>>, t: Throwable) {
                checkoutOrderEvent.postValue(CheckoutEvent(false))
            }

        })
    }

    fun finalizeOrder(orderId: Long) {
        api.finalizeOrder(orderId).enqueue(object: Callback<ServerResponse<Void>>{
            override fun onResponse(call: Call<ServerResponse<Void>>, response: Response<ServerResponse<Void>>) {
                if(response.isSuccessful){
                    orderManager.finalizeOrder()
                    finalizeOrderEvent.postValue(FinalizeEvent(true))
                }else{
                    finalizeOrderEvent.postValue(FinalizeEvent(false))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Void>>, t: Throwable) {
                finalizeOrderEvent.postValue(FinalizeEvent(false))
            }

        })
    }


}
