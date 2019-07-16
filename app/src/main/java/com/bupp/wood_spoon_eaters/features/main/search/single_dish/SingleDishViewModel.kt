package com.bupp.wood_spoon_eaters.features.main.search.single_dish

import android.util.Log
import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.features.main.search.SearchViewModel
import com.bupp.wood_spoon_eaters.managers.EaterAddressManager
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class SingleDishViewModel(val api: ApiService, val orderManager: OrderManager, val eaterAddressManager: EaterAddressManager) : ViewModel() {

    data class DishDetailsEvent(val isSuccess: Boolean = false, val dish: FullDish?)
    val dishDetailsEvent: SingleLiveEvent<DishDetailsEvent> = SingleLiveEvent()

    data class PostOrderEvent(val isSuccess: Boolean = false, val order: Search?)
    val postOrderEvent: SingleLiveEvent<PostOrderEvent> = SingleLiveEvent()

    fun getFullDish(menuItemId: Long) {
        api.getMenuItemsDetails(menuItemId).enqueue(object: Callback<ServerResponse<FullDish>> {
            override fun onResponse(call: Call<ServerResponse<FullDish>>, response: Response<ServerResponse<FullDish>>) {
                if(response.isSuccessful){
                    Log.d("wowSearchVM","getMenuItemsDetails success")
                    val dish = response.body()?.data
                    dishDetailsEvent.postValue(DishDetailsEvent(true, dish))
                }else{
                    Log.d("wowSearchVM","getMenuItemsDetails fail")
                    dishDetailsEvent.postValue(DishDetailsEvent(false, null))
                }
            }

            override fun onFailure(call: Call<ServerResponse<FullDish>>, t: Throwable) {
                Log.d("wowSearchVM","getMenuItemsDetails big fail: ${t.message}")
                dishDetailsEvent.postValue(DishDetailsEvent(false, null))
            }
        })
    }

    fun addToCart(cookingSlotId: Long? = null, dishId: Long? = null, quantity: Int = 1, removedIngredients: ArrayList<Long>? = null, note: String? = null, tipPercentage: Float? = null,
                  tipAmount: String? = null, promoCodeId: Long? = null) {
//        val cookingSlotId = cookingSlotId
        val deliveryAddressId = eaterAddressManager.getLastChosenAddress()?.id
        val orderItem = initOrderItemsList(dishId, quantity, removedIngredients, note)

        orderManager.updateOrderRequest(
            cookingSlotId = cookingSlotId,
            deliveryAddressId = deliveryAddressId,
            tipPercentage = tipPercentage,
            tipAmount = tipAmount,
            promoCodeId = promoCodeId)

        orderManager.addOrderItem(orderItem)
        api.postOrder(orderManager.currentOrderRequest).enqueue(object: Callback<ServerResponse<Search>>{
            override fun onResponse(call: Call<ServerResponse<Search>>, response: Response<ServerResponse<Search>>) {
                if(response.isSuccessful){
                    val search = response.body()?.data
                    Log.d("wowFeedVM","postOrder success: ${search.toString()}")
                    postOrderEvent.postValue(PostOrderEvent(true, search))
                }else{
                    Log.d("wowFeedVM","postOrder fail")
                    postOrderEvent.postValue(PostOrderEvent(false,null))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Search>>, t: Throwable) {
                Log.d("wowFeedVM","postOrder big fail")
                postOrderEvent.postValue(PostOrderEvent(false,null))
            }
        })
        Log.d("wowSingleDishVM","addToCart finish")
    }

    private fun initOrderItemsList(dishId: Long? = null, quantity: Int = 1, removedIngredients: ArrayList<Long>? = null, note: String? = null): OrderItem {
        return OrderItem(dishId = dishId, quantity = quantity, removedIndredientsIds = removedIngredients, notes = note)
    }

    fun getUserChosenDeliveryDate(): Date? {
        if(orderManager.getLastOrderTime() != null){
            return orderManager.getLastOrderTime()
        }
        return Date()
    }
}
