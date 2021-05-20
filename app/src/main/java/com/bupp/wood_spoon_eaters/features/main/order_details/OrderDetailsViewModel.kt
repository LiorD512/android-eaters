package com.bupp.wood_spoon_eaters.features.main.order_details

import android.util.Log
import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderDetailsViewModel(val api: ApiService) : ViewModel() {


    val orderDetails: SingleLiveEvent<OrderDetailsEvent> = SingleLiveEvent()
    data class OrderDetailsEvent(val isSuccess: Boolean, val order: Order?)

    fun getOrderDetails(orderId: Long) {
        api.getOrderById(orderId).enqueue(object: Callback<ServerResponse<Order>> {
            override fun onResponse(call: Call<ServerResponse<Order>>, response: Response<ServerResponse<Order>>) {
                if(response.isSuccessful){
                    val order = response.body()?.data
                    if(order != null){
                        orderDetails.postValue(OrderDetailsEvent(true, order))
                    }else{
                        orderDetails.postValue(OrderDetailsEvent(false, null))
                    }
                }else{
                    Log.d("wowFeedVM","postOrder fail")
                    orderDetails.postValue(OrderDetailsEvent(false, null))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Order>>, t: Throwable) {
                Log.d("wowFeedVM","postOrder big fail")
                orderDetails.postValue(OrderDetailsEvent(false, null))
            }
        })
    }

}
