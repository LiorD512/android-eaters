package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish

import android.util.Log
import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class SingleDishViewModel(val api: ApiService, val settings: AppSettings, val orderManager: OrderManager, val eaterDataManager: EaterDataManager) : ViewModel() {

    data class DishDetailsEvent(val isSuccess: Boolean = false, val dish: FullDish?)
    val dishDetailsEvent: SingleLiveEvent<DishDetailsEvent> = SingleLiveEvent()

    data class PostOrderEvent(val isSuccess: Boolean = false, val order: Order?)
    val postOrderEvent: SingleLiveEvent<PostOrderEvent> = SingleLiveEvent()

    fun getFullDish(menuItemId: Long) {
        val feedRequest = getFeedRequest()
        api.getMenuItemsDetails(menuItemId = menuItemId, lat = feedRequest.lat, lng = feedRequest.lng, addressId = feedRequest.addressId, timestamp = feedRequest.timestamp)
            .enqueue(object: Callback<ServerResponse<FullDish>> {
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

    private fun getFeedRequest(): FeedRequest {
        var feedRequest = FeedRequest()
        //address
        val currentAddress = eaterDataManager.getLastChosenAddress()
        if(eaterDataManager.isUserChooseSpecificAddress()){
            feedRequest.addressId = currentAddress?.id
        }else{
            feedRequest.lat = currentAddress?.lat
            feedRequest.lng = currentAddress?.lng
        }

        //time
        feedRequest.timestamp = eaterDataManager.getLastOrderTimeParam()

        return feedRequest
    }

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
                    Log.d("wowFeedVM","postOrder success: ${order.toString()}")
                    postOrderEvent.postValue(PostOrderEvent(true, order))
                }else{
                    Log.d("wowFeedVM","postOrder fail")
                    postOrderEvent.postValue(PostOrderEvent(false,null))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Order>>, t: Throwable) {
                Log.d("wowFeedVM","postOrder big fail")
                postOrderEvent.postValue(PostOrderEvent(false,null))
            }
        })
        Log.d("wowSingleDishVM","addToCart finish")
    }

    private fun initOrderItemsList(dishId: Long? = null, quantity: Int = 1, removedIngredients: ArrayList<Long>? = null, note: String? = null): OrderItemRequest {
        return OrderItemRequest(dishId = dishId, quantity = quantity, removedIndredientsIds = removedIngredients, notes = note)
    }

    fun updateChosenDeliveryDate(newChosenDate: Date) {
        eaterDataManager.orderTime = newChosenDate
    }

    fun getUserChosenDeliveryDate(): Date? {
        if(eaterDataManager.getLastOrderTime() != null){
            return eaterDataManager.getLastOrderTime()
        }
        return Date()
    }

    fun getDropoffLocation(): String? {
        return eaterDataManager.currentEater?.addresses?.first()?.getDropoffLocationStr()
    }


}
