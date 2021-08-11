package com.bupp.wood_spoon_eaters.managers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSectionSingleDish
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import com.bupp.wood_spoon_eaters.model.CookingSlot
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.OrderItemRequest
import com.bupp.wood_spoon_eaters.model.OrderRequest
import com.bupp.wood_spoon_eaters.repositories.OrderRepository

class CartManager(
    private val feedDataManager: FeedDataManager,
    private val deliveryTimeManager: DeliveryTimeManager,
    private val orderRepository: OrderRepository,
    private val eventsManager: EventsManager
) {

    // global params -
    private var currentOrderResponse: Order? = null

    var currentCookingSlot: CookingSlot? = null
    val deliverAt = deliveryTimeManager.getTempDeliveryTimeStamp()
    val deliveryAddressId = feedDataManager.getFinalAddressLiveDataParam().value?.id

    val orderLiveData = MutableLiveData<Order>()

    fun getCurrentCartData() = orderLiveData


    private fun buildOrderRequest(cart: List<OrderItemRequest>? = null): OrderRequest {
        return OrderRequest(
            cookingSlotId = currentCookingSlot?.id,
            deliveryAt = deliverAt,
            deliveryAddressId = deliveryAddressId,
            orderItemRequests = cart
        )
    }

    fun addItemRequest(dishId: Long, quantity: Int, note: String? = null){
        val orderItemRequest = OrderItemRequest(dishId, quantity = quantity, notes = note)
        val orderRequest = buildOrderRequest(listOf(orderItemRequest))
        postOrUpdateOrder(orderRequest)
    }

    private fun postOrUpdateOrder(orderRequest: OrderRequest) {
        if(currentOrderResponse != null){
            //update
        }else{
            //post new order
        }
    }

    suspend fun postNewOrder(orderRequest: OrderRequest){
        Log.d(OldCartManager.TAG, "postNewOrUpdateCart.. posting new order")
        val result = orderRepository.postNewOrder(orderRequest)
        if(result.type == OrderRepository.OrderRepoStatus.POST_ORDER_SUCCESS){
            result.data?.let{
                orderLiveData.postValue(it)
            }
        }
//        eventsManager.logEvent(Constants.EVENT_ADD_DISH, getAddDishData(result.data?.id))
    }

    fun updateItemRequest(item: DishSectionSingleDish) {

    }



    //update dish
}