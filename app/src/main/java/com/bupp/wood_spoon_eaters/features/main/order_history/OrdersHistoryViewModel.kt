package com.bupp.wood_spoon_eaters.features.main.order_history

import android.util.Log
import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.Report
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrdersHistoryViewModel(val api: ApiService) : ViewModel() {

    val TAG = "wowOrderHistoryVM"
    val getOrdersEvent: SingleLiveEvent<OrderHistoryEvent> = SingleLiveEvent()
    data class OrderHistoryEvent(val isSuccess: Boolean, val orderHistory: ArrayList<Order>? = null)

    fun getOrderHistory(){
        api.getOrders().enqueue(object: Callback<ServerResponse<ArrayList<Order>>>{
            override fun onResponse(call: Call<ServerResponse<ArrayList<Order>>>, response: Response<ServerResponse<ArrayList<Order>>>) {
                if(response.isSuccessful){
                    Log.d(TAG, "getOrders success")
                    val orderHistory = response.body()?.data
                    getOrdersEvent.postValue(OrderHistoryEvent(true, orderHistory))
                }else{
                    Log.d(TAG, "getOrders fail")
                    getOrdersEvent.postValue(OrderHistoryEvent(false))
                }
            }

            override fun onFailure(call: Call<ServerResponse<ArrayList<Order>>>, t: Throwable) {
                Log.d(TAG, "getOrders big fail")
                getOrdersEvent.postValue(OrderHistoryEvent(false))
            }


        })
    }

}
