package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.promo_code

import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PromoCodeViewModel(val api: ApiService,val orderManager: OrderManager) : ViewModel() {


    val promoCodeEvent: SingleLiveEvent<PromoCodeEvent> = SingleLiveEvent()
    data class PromoCodeEvent(val isSuccess: Boolean = false)

    fun savePromoCode(code: String) {
        orderManager.updateOrderRequest(promoCode = code)
        api.updateOrder(orderManager.curOrderResponse!!.id, orderManager.getOrderRequest()).enqueue(object: Callback<ServerResponse<Order>>{
            override fun onResponse(call: Call<ServerResponse<Order>>, response: Response<ServerResponse<Order>>) {
                if(response.isSuccessful){
                    val order = response.body()?.data
                    orderManager.setOrderResponse(order)
                    promoCodeEvent.postValue(PromoCodeEvent(true))
                }else{
                    promoCodeEvent.postValue(PromoCodeEvent(false))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Order>>, t: Throwable) {
                promoCodeEvent.postValue(PromoCodeEvent(false))
            }

        })
    }
}
