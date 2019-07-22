package com.bupp.wood_spoon_eaters.features.new_order

import android.util.Log
import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class NewOrderViewModel(val api: ApiService, val orderManager: OrderManager, val eaterDataManager: EaterDataManager) : ViewModel() {

    data class PostOrderEvent(val isSuccess: Boolean = false, val order: Order?)
    val postOrderEvent: SingleLiveEvent<PostOrderEvent> = SingleLiveEvent()



    fun addToCart(cookingSlotId: Long? = null, dishId: Long? = null, quantity: Int = 1, removedIngredients: ArrayList<Long>? = null, note: String? = null, tipPercentage: Float? = null,
                  tipAmount: String? = null, promoCodeId: Long? = null) {
//        val cookingSlotId = cookingSlotId
        val deliveryAddress = eaterDataManager.getLastChosenAddress()
        val orderItem = initOrderItemsList(dishId, quantity, removedIngredients, note)

        orderManager.updateOrderRequest(
            cookingSlotId = cookingSlotId,
            deliveryAddress = deliveryAddress,
            tipPercentage = tipPercentage,
            tipAmount = tipAmount,
            promoCodeId = promoCodeId)

        orderManager.addOrderItem(orderItem)
        api.postOrder(orderManager.currentOrderRequest).enqueue(object: Callback<ServerResponse<Order>>{
            override fun onResponse(call: Call<ServerResponse<Order>>, response: Response<ServerResponse<Order>>) {
                if(response.isSuccessful){
                    val order = response.body()?.data
                    orderManager.setOrderResponse(order)
                    Log.d("wowNewOrderVM","postOrder success: ${order.toString()}")
                    postOrderEvent.postValue(PostOrderEvent(true, order))
                }else{
                    Log.d("wowNewOrderVM","postOrder fail")
                    postOrderEvent.postValue(PostOrderEvent(false,null))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Order>>, t: Throwable) {
                Log.d("wowNewOrderVM","postOrder big fail")
                postOrderEvent.postValue(PostOrderEvent(false,null))
            }
        })
        Log.d("wowNewOrderVM","addToCart finish")
    }

    private fun initOrderItemsList(dishId: Long? = null, quantity: Int = 1, removedIngredients: ArrayList<Long>? = null, note: String? = null): OrderItemRequest {
        return OrderItemRequest(dishId = dishId, quantity = quantity, removedIndredientsIds = removedIngredients, notes = note)
    }



}
