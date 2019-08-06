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

    data class GetReviewsEvent(val isSuccess: Boolean = false, val reviews: Review? = null)
    val getReviewsEvent: SingleLiveEvent<GetReviewsEvent> = SingleLiveEvent()

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

        orderManager.initNewOrder()
        orderManager.updateOrderRequest(
            cookingSlotId = cookingSlotId,
            deliveryAddress = deliveryAddress,
            tipPercentage = tipPercentage,
            tipAmount = tipAmount,
            promoCodeId = promoCodeId)

        orderManager.addOrderItem(orderItem)
        api.postOrder(orderManager.getOrderRequest()).enqueue(object: Callback<ServerResponse<Order>>{
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

    fun getDishReview(cookId: Long) {
        api.getDishReview(cookId).enqueue(object: Callback<ServerResponse<Review>>{
            override fun onResponse(call: Call<ServerResponse<Review>>, response: Response<ServerResponse<Review>>) {
                if(response.isSuccessful){
                    val reviews = response.body()?.data
                    Log.d("wowFeedVM","getDishReview success")
                    getReviewsEvent.postValue(GetReviewsEvent(true, reviews))
                }else{
                    Log.d("wowFeedVM","getDishReview fail")
                    getReviewsEvent.postValue(GetReviewsEvent(false))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Review>>, t: Throwable) {
                Log.d("wowFeedVM","getDishReview big fail: ${t.message}")
                getReviewsEvent.postValue(GetReviewsEvent(false))
            }
        })
    }


    val hasOpenOrder: SingleLiveEvent<HasOpenOrder> = SingleLiveEvent()
    data class HasOpenOrder(val hasOpenOrder: Boolean, val cookInCart: Cook? = null, val currentShowingCook: Cook? = null)

    fun checkForOpenOrder(currentShowingCook: Cook) {
        if(orderManager.haveCurrentActiveOrder()){
            val inCartOrder = orderManager.curOrderResponse
            val cookInCart = inCartOrder?.cook
            if(currentShowingCook.id != cookInCart?.id){
                //if the showing dish's (cook) is the same as the in-cart order's cook
                hasOpenOrder.postValue(HasOpenOrder(true, cookInCart, currentShowingCook))
            }else{
                hasOpenOrder.postValue(HasOpenOrder(false))
            }
        }else{
            hasOpenOrder.postValue(HasOpenOrder(false))
        }
    }

    fun clearCurrentOpenOrder() {
        orderManager.clearCurrentOrder()
    }


}
