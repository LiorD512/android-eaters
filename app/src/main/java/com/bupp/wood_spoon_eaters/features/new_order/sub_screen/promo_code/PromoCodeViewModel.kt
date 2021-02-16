package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.promo_code

import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.model.WSError
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.BaseCallback


class PromoCodeViewModel(val api: ApiService,val orderManager: OrderManager) : ViewModel() {


    val promoCodeEvent: SingleLiveEvent<PromoCodeEvent> = SingleLiveEvent()
    val errorEvent: SingleLiveEvent<List<WSError>> = SingleLiveEvent()
    data class PromoCodeEvent(val isSuccess: Boolean = false)

    fun savePromoCode(code: String) {
        orderManager.updateOrderRequest(promoCode = code)
        api.updateOrder(orderManager.curOrderResponse!!.id, orderManager.getPromoCodeOrderRequest()).enqueue(object: BaseCallback<ServerResponse<Order>>(){
            override fun onSuccess(result: ServerResponse<Order>) {
                val order = result.data
                orderManager.setOrderResponse(order)
                promoCodeEvent.postValue(PromoCodeEvent(true))
            }

            override fun onError(errors: List<WSError>) {
                errorEvent.postValue(errors)
            }



        })
    }


}
