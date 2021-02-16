package com.bupp.wood_spoon_eaters.managers

import android.util.Log
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import java.util.*

class CartManager(private val orderRepository: OrderRepository, private val feedDataManager: FeedDataManager, private val deliveryTimeManager: DeliveryTimeManager) {

    var currentShowingDish: FullDish? = null
    private var currentOrderRequest: OrderRequest? = null
    private var currentOrderResponse: Order? = null

    private var currentOrderItem = OrderItemRequest()
    private val cart: MutableList<OrderItemRequest> = mutableListOf()



    data class GetFullDishResult(
        val fullDish: FullDish,
        val isAvailable: Boolean,
        val startingTime: Date?,
        val isSoldOut: Boolean
    )

//    val getFeedRequestLiveData = feedDataManager.getFeedRequestLiveData()

    suspend fun getFullDish(menuItemId: Long): GetFullDishResult? {
        val feedRequest = feedDataManager.getLastFeedRequest()
        val result = orderRepository.getFullDish(menuItemId, feedRequest)
        result.data?.let{
            updateCurrentOrderItem(OrderItemRequest(dishId = it.id, quantity = 1))
            this.currentShowingDish = it
            return GetFullDishResult(
                it,
                isAvailable = checkCookingSlotAvailability(),
                startingTime = getStartingDate(),
                isSoldOut = checkDishSellout()
            )
        }
        //inspect result log if reach here.
        return null
    }

    private fun checkCookingSlotAvailability(): Boolean {
        currentShowingDish?.let {
            val orderFrom: Date? = it.menuItem?.cookingSlot?.orderFrom
            val start: Date? = it.menuItem?.cookingSlot?.startsAt
            val end: Date? = it.menuItem?.cookingSlot?.endsAt
            var userSelection: Date? = deliveryTimeManager.getDeliveryTimeDate()

            if (start == null || end == null) {
                return false
            }
            if (userSelection == null) {
                //in this case order is ASAP - then check from starting time and not orderingFrom time
                userSelection = Date()
                return (userSelection.equals(start) || userSelection.equals(end)) || (userSelection.after(start) && userSelection.before(end))
            }
            return (userSelection.equals(orderFrom) || userSelection.equals(end)) || (userSelection.after(orderFrom) && userSelection.before(end))
        }
        return true
    }

    private fun getStartingDate(): Date? {
        var newDate = Date()
        currentShowingDish?.menuItem?.cookingSlot?.orderFrom?.let {
            if (it.after(newDate)) {
                newDate = it
            }
        }
        return newDate
    }

    private fun checkDishSellout(): Boolean {
        currentShowingDish?.let {
            val quantity = it.menuItem?.quantity
            val unitsSold = it.menuItem?.unitsSold
            quantity?.let {
                unitsSold?.let {
                    return ((quantity - unitsSold <= 0))
                }
            }
        }
        return false
    }

    data class CartStatus(val type: CartStatusEventType, val inCartCookName: String? = null, val currentShowingCookName: String? = null)
    enum class CartStatusEventType{
        NEW_ORDER,
        ADD_TO_ORDER_FOR_CURRENT_COOKING_SLOT,
        DIFFERENT_COOKING_SLOT
    }

    //check order status - is there any open order?
    fun getOrderStatus(): CartStatus {
         currentOrderResponse?.let{ orderResponse ->
            val currentCookingSlotId = currentShowingDish?.menuItem?.cookingSlot?.id
            val currentShowingCookName = currentShowingDish?.cook?.getFullName()
            val inCartCookingSlot = orderResponse.cookingSlot
            val inCartCookName = orderResponse.cook.getFullName()
            inCartCookingSlot.let { inCartCookingSlot ->
                if (inCartCookingSlot.id != currentCookingSlotId) {
                    //if the showing dish's (cook) is the same as the in-cart order's cook
                    return CartStatus(CartStatusEventType.DIFFERENT_COOKING_SLOT, inCartCookName, currentShowingCookName)
                } else {
                    return CartStatus(CartStatusEventType.ADD_TO_ORDER_FOR_CURRENT_COOKING_SLOT)
                }
            }
        }
        return CartStatus(CartStatusEventType.NEW_ORDER)
    }


    suspend fun addNewItemToCart(): OrderRepository.OrderRepoResult<Order> {
        cart.add(0, currentOrderItem)
        Log.d(TAG, "addNewItemToCart: $currentOrderItem")

        val cookingSlotId = currentShowingDish?.menuItem?.cookingSlot?.id
        val deliverAt = deliveryTimeManager.getDeliveryTimestamp()
        val deliveryAddressId = feedDataManager.getFinalAddressLiveDataParam().value?.id

        val orderRequest = OrderRequest(
            cookingSlotId = cookingSlotId,
            deliveryAt = deliverAt,
            deliveryAddressId = deliveryAddressId,
            orderItemRequests = cart)
        val result =  orderRepository.postNewOrder(orderRequest)
        this.currentOrderResponse = result.data
        return result
    }


    fun updateCurrentOrderItem(orderItemRequest: OrderItemRequest){
        currentOrderItem = OrderItemRequest(
            dishId = orderItemRequest.dishId ?: currentOrderItem.dishId,
            notes = orderItemRequest.notes ?: currentOrderItem.notes,
            quantity = orderItemRequest.quantity ?: currentOrderItem.quantity)
        Log.d(TAG, "updateCurrentOrderItem: $currentOrderItem")
    }

    fun clearCart(){
        cart.clear()
    }



    //Additional dishes bottom sheet
    fun shouldShowAdditionalDishesDialog(): Boolean {
        return cart.size == 1
    }
    fun getCurrentOrderItems(): List<OrderItem>? {
        currentOrderResponse?.let{
            return it.orderItems
        }
        return null
    }
    fun getAdditionalDishes(): List<Dish>? {
        currentShowingDish?.let{
            return it.cook.dishes
        }
        return null
    }


//    //check if there is an order in cart from diffrent cook
//    fun checkForDifferentOpenOrder(): Boolean {
//        currentOrderResponse?.let{
//            val currentCookingSlotId = currentShowingDish?.menuItem?.cookingSlot?.id
//            val inCartCookingSlot = it.cookingSlot
//            inCartCookingSlot.let { it ->
//                if (it.id != currentCookingSlotId) {
//                    return true
//                }
//            }
//        }
//        return false
//    }

    companion object{
        const val TAG = "wowCartManager"
    }

}